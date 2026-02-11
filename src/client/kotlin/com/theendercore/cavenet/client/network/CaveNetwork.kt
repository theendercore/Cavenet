package com.theendercore.cavenet.client.network

import com.theendercore.cavenet.client.init.CNNetworkManager
import com.theendercore.cavenet.client.network.node.DoorNode
import com.theendercore.cavenet.client.network.node.ExploreNode
import com.theendercore.cavenet.client.network.node.ExploreNode.Companion.ExploreState
import com.theendercore.cavenet.client.sendMessage
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import java.util.*

class CaveNetwork(val direction: Direction, val pos: BlockPos, var phase: NetPhase) {
    constructor(pos: BlockPos, direction: Direction) : this(direction, pos, NetPhase.OPENING_DOOR) {
        doorPos.add(pos)
        DoorNode(pos, this)
    }

    val id: UUID = UUID.randomUUID()

    val doorMap = Direction.entries.filter { it.axis != direction.axis }


    val doorPos = mutableListOf<BlockPos>()
    val explorePos = mutableListOf<BlockPos>()

    fun tick(world: ClientLevel) {
        when (phase) {
            NetPhase.OPENING_DOOR -> {
                var mod = false
                for (nodePos in doorPos.toList()) {
                    val node = CNNetworkManager.nodeMap[nodePos]
                    if (node !is DoorNode) {
                        println("Network has bad DoorNode pos: [$nodePos]")
                        continue
                    }
                    if (!node.shouldTick()) continue

                    if (!CNNetworkManager.canNodeExplore(world, nodePos)) {
                        CNNetworkManager.nodeMap.remove(nodePos)
                        doorPos.remove(nodePos)
                        continue
                    }

                    for (dir in doorMap) {
                        val sidePos = nodePos.relative(dir)
                        if (!CNNetworkManager.canNodeExplore(world, sidePos)) continue
                        val otherNode = CNNetworkManager.nodeMap[sidePos]
                        if (otherNode != null) continue

                        doorPos.add(sidePos)
                        DoorNode(sidePos, this)
                        mod = true
//                        return
                    }
                    node.isActive = false
                }

                if (!mod) startExploring(world)
            }

            NetPhase.EXPLORING_CAVE -> {
                var mod = false
                for (nodePos in explorePos.toList()) {
                    val node = CNNetworkManager.nodeMap[nodePos]
                    if (node !is ExploreNode) {
                        println("Network has bad ExploreNode pos: [$nodePos]")
                        continue
                    }
                    if (!node.shouldTick()) continue

                    if (!CNNetworkManager.canNodeExplore(world, nodePos)) {
                        CNNetworkManager.nodeMap.remove(nodePos)
                        explorePos.remove(nodePos)
                        continue
                    }

                    val edges = mutableListOf<Direction>()
                    for (dir in Direction.entries) {
                        val sidePos = nodePos.relative(dir)
                        if (!CNNetworkManager.canNodeExplore(world, sidePos)) {
                            edges.add(dir)
                            continue
                        }
                        val otherNode = CNNetworkManager.nodeMap[sidePos]
                        if (otherNode != null) {
                            val net = otherNode.network()
                            if (otherNode is DoorNode && net != this && net.phase == NetPhase.COMPLETE) {
                                for (otherNetNode in net.doorPos.toList()) {
                                    explorePos.add(otherNetNode)
                                    ExploreNode(otherNetNode, this, ExploreState.EDGE).edges =
                                        listOf(net.direction.opposite)
                                    net.doorPos.remove(otherNetNode)
                                }
                                CNNetworkManager.toRemove.add(net.id)
                                mod = true
                            }
                            continue
                        }

                        explorePos.add(sidePos)
                        ExploreNode(sidePos, this)
                        mod = true
//                        return
                    }
                    node.state = if (edges.isNotEmpty()) ExploreState.EDGE else ExploreState.MIDDLE
                    if (edges.isNotEmpty()) node.edges = edges

                }

                if (!mod) markAsDone()
            }

            NetPhase.COMPLETE -> Unit
            NetPhase.FROZEN -> Unit
        }
    }

    private fun markAsDone() {
        phase = NetPhase.COMPLETE
        for (nodePos in explorePos.toList()) {
            val node = CNNetworkManager.nodeMap[nodePos]
            if (node is ExploreNode && node.edges.isEmpty()) {
                CNNetworkManager.nodeMap.remove(nodePos)
                explorePos.remove(nodePos)
            }
        }
        sendMessage("Net [$pos] Done!")
    }

    private fun startExploring(world: ClientLevel) {
        phase = NetPhase.EXPLORING_CAVE
        var offPos: BlockPos
        for (dPos in doorPos) {
            offPos = dPos.relative(direction)
            if (CNNetworkManager.canNodeExplore(world, offPos) && CNNetworkManager.nodeMap[offPos] == null) {
                explorePos.add(offPos)
                ExploreNode(offPos, this)
            }
        }
    }

    fun size() = doorPos.size + explorePos.size
    fun isEmpty() = doorPos.isEmpty() && explorePos.isEmpty()
    fun nodePositions() = doorPos + explorePos

    fun clear() {
        for (nodePos in (doorPos + explorePos)) {
            CNNetworkManager.nodeMap.remove(nodePos)
        }
        doorPos.clear()
        explorePos.clear()
    }

    override fun toString(): String = buildString {
        append("Dir: $direction, ")
        append("Pos: [${pos.x}, ${pos.y}, ${pos.z}], ")
        append("Phase: ${phase.name.lowercase()}")
    }

    enum class NetPhase {
        OPENING_DOOR,
        EXPLORING_CAVE,
        COMPLETE,
        FROZEN
    }
}
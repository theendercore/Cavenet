package com.theendercore.cavenet.client.network

import com.theendercore.cavenet.client.init.CNLogic
import com.theendercore.cavenet.client.network.node.DoorNode
import com.theendercore.cavenet.client.network.node.ExploreNode
import com.theendercore.cavenet.client.network.node.ExploreNode.Companion.ExploreState
import com.theendercore.cavenet.client.sendMessage
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction

class CaveNetwork(val direction: Direction, val pos: BlockPos, var phase: NetPhase) {
    constructor(pos: BlockPos, direction: Direction) : this(direction, pos, NetPhase.OPENING_DOOR) {
        doorPos.add(pos)
        DoorNode(pos, this)
    }

    val doorMap = Direction.entries.filter { it.axis != direction.axis }


    val doorPos = mutableListOf<BlockPos>()
    val explorePos = mutableListOf<BlockPos>()

    fun tick(world: ClientLevel) {
        when (phase) {
            NetPhase.OPENING_DOOR -> {
                for (nodePos in doorPos.toList()) {
                    val node = CNLogic.nodeMap[nodePos]
                    if (node !is DoorNode) {
                        println("Network has bad DoorNode pos: [$nodePos]")
                        continue
                    }
                    if (!node.shouldTick()) continue

                    if (!CNLogic.canNodeExplore(world, nodePos)) {
                        CNLogic.nodeMap.remove(nodePos)
                        doorPos.remove(nodePos)
                        continue
                    }

                    for (dir in doorMap) {
                        val sidePos = nodePos.relative(dir)
                        if (!CNLogic.canNodeExplore(world, sidePos)) continue
                        val otherNode = CNLogic.nodeMap[sidePos]
                        if (otherNode != null) continue

                        doorPos.add(sidePos)
                        DoorNode(sidePos, this)
                        return
                    }
                    node.isActive = false
                }

                startExploring(world)
            }

            NetPhase.EXPLORING_CAVE -> {
                for (nodePos in explorePos.toList()) {
                    val node = CNLogic.nodeMap[nodePos]
                    if (node !is ExploreNode) {
                        println("Network has bad ExploreNode pos: [$nodePos]")
                        continue
                    }
                    if (!node.shouldTick()) continue

                    if (!CNLogic.canNodeExplore(world, nodePos)) {
                        CNLogic.nodeMap.remove(nodePos)
                        explorePos.remove(nodePos)
                        continue
                    }

                    var hasEdge = false
                    for (dir in Direction.entries) {
                        val sidePos = nodePos.relative(dir)
                        if (!CNLogic.canNodeExplore(world, sidePos)) {
                            hasEdge = true
                            continue
                        }
                        val otherNode = CNLogic.nodeMap[sidePos]
                        if (otherNode != null) continue

                        explorePos.add(sidePos)
                        ExploreNode(sidePos, this)
                        return
                    }
                    node.state = if (hasEdge) ExploreState.EDGE else ExploreState.MIDDLE
                }
                markAsDone()
            }

            NetPhase.COMPLETE -> Unit
            NetPhase.FROZEN -> Unit
        }
    }

    private fun markAsDone() {
        phase = NetPhase.COMPLETE
        for (nodePos in explorePos) {
            CNLogic.nodeMap.remove(nodePos)
        }
        explorePos.clear()
        sendMessage("Net [$pos] Done!")
    }

    private fun startExploring(world: ClientLevel) {
        phase = NetPhase.EXPLORING_CAVE
        var offPos: BlockPos
        for (dPos in doorPos) {
            offPos = dPos.relative(direction)
            if (CNLogic.canNodeExplore(world, offPos)) {
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
            CNLogic.nodeMap.remove(nodePos)
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
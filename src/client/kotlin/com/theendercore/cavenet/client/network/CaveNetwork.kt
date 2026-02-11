package com.theendercore.cavenet.client.network

import com.theendercore.cavenet.client.init.CNLogic
import com.theendercore.cavenet.client.network.node.DoorNode
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction

class CaveNetwork(val direction: Direction, val pos: BlockPos, val phase: NetPhase) {
    constructor(pos: BlockPos, direction: Direction) : this(direction, pos, NetPhase.EXPLORING_CAVE) {
        doorPos.add(pos)
        DoorNode(pos, this)
    }

    val doorMap = Direction.entries.filter { it == direction || it == direction.opposite }


    val doorPos = mutableListOf<BlockPos>()
    val explorePos = mutableListOf<BlockPos>()

    fun tick(world: ClientLevel) {
        when (phase) {
            NetPhase.OPENING_DOOR -> {
                for (nodePos in doorPos) {
                    val node = CNLogic.nodeMap[nodePos]
                    if (node !is DoorNode) {
                        println("Network has bad DoorNode pos: [$nodePos]")
                        continue
                    }
                    if (!node.isActive) continue

                    for (dir in doorMap) {
                        val sidePos = nodePos.relative(dir)
                        if (!CNLogic.canNodeExplore(world, sidePos)) continue
                        if (doorPos.contains(sidePos)) continue

                        DoorNode(sidePos, this)
                        return
                    }
                    node.isActive = false
                }
            }

            NetPhase.EXPLORING_CAVE -> Unit
            NetPhase.COMPLETE -> Unit
            NetPhase.FROZEN -> Unit
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
        append("Pos: [${pos.x}, ${pos.y}, ${pos.z}]")
        append("Phase: ${phase.name.lowercase()}")
    }

    enum class NetPhase {
        OPENING_DOOR,
        EXPLORING_CAVE,
        COMPLETE,
        FROZEN
    }
}
package com.theendercore.cavenet.client.network

import com.theendercore.cavenet.client.init.CNLogic
import com.theendercore.cavenet.client.network.node.DoorNode
import com.theendercore.cavenet.client.network.node.ExploreNode
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction

class CaveNetwork(val direction: Direction, val pos: BlockPos, val phase: NetPhase) {
    constructor(pos: BlockPos, direction: Direction) : this(direction, pos, NetPhase.EXPLORING_CAVE) {
        doorNodes[pos] = DoorNode(this)
    }

    val doorMap = Direction.entries.filter { it == direction || it == direction.opposite }


    val doorNodes = mutableMapOf<BlockPos, DoorNode>()
    val exploreNodes = mutableMapOf<BlockPos, ExploreNode>()

    fun tick(world: ClientLevel) = when (phase) {
        NetPhase.OPENING_DOOR -> {
            for ((nodePos, node) in doorNodes) {
                if (!node.isActive) continue

                for (dir in doorMap) {
                    val sidePos = nodePos.relative(dir)
                    if (!CNLogic.canNodeExplore(world, sidePos)) continue
                    if (doorNodes.contains(sidePos)) continue

                    doorNodes[sidePos] = DoorNode(this)
                    continue
                }
            }
        }

        NetPhase.EXPLORING_CAVE -> Unit
        NetPhase.COMPLETE -> Unit
        NetPhase.FROZEN -> Unit
    }

    fun size() = doorNodes.size + exploreNodes.size

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
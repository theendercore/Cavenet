package com.theendercore.cavenet.client.network.node

import com.theendercore.cavenet.client.CavenetClient.nodes
import com.theendercore.cavenet.client.init.CNLogic
import com.theendercore.cavenet.client.network.CaveNetwork
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction

class ExploreNode(val net: CaveNetwork, var state: ExploreState = ExploreState.EXPLORING) : INode {
    override fun shouldTick(): Boolean = state != ExploreState.INACTIVE
    override fun network(): CaveNetwork = net

    /*fun tick(world: ClientLevel, pos: BlockPos) {
        if (!CNLogic.canNodeExplore(world, pos)) {
            nodes.remove(pos)
            return
        }
        when (state) {
            ExploreState.EXPLORING -> {
                for (dir in Direction.entries) {
                    val sidePos = pos.relative(dir)
                    if (!CNLogic.canNodeExplore(world, sidePos)) continue
                    val node = nodes[sidePos]
//                    if (node is DoorNode && node.direction == dir.opposite) continue
                    if (node is ExploreNode) continue

                    nodes[sidePos] = ExploreNode()
                    return
                }
                state = ExploreState.INACTIVE
            }

            ExploreState.DELETING -> {
            }

            ExploreState.INACTIVE -> Unit
        }
    }*/

    override fun shouldRender(): Boolean = false

    companion object {
        enum class ExploreState {
            EXPLORING,
            INACTIVE,
            DELETING
        }
    }
}
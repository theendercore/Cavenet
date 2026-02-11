package com.theendercore.cavenet.client.network.node

import com.theendercore.cavenet.client.init.CNLogic
import com.theendercore.cavenet.client.network.CaveNetwork
import net.minecraft.core.BlockPos

class ExploreNode(pos: BlockPos, val net: CaveNetwork, var state: ExploreState = ExploreState.UNDETERMINED) : INode {

    init {
        CNLogic.nodeMap[pos] = this
    }

    override fun shouldTick(): Boolean = state == ExploreState.UNDETERMINED
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

    override fun shouldRender(): Boolean = state != ExploreState.MIDDLE

    companion object {
        enum class ExploreState {
            UNDETERMINED,
            MIDDLE,
            EDGE
        }
    }
}
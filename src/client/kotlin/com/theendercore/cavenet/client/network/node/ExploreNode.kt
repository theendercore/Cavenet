package com.theendercore.cavenet.client.network.node

import com.theendercore.cavenet.client.init.CNNetworkManager
import com.theendercore.cavenet.client.network.CaveNetwork
import net.minecraft.core.BlockPos

class ExploreNode(pos: BlockPos, val net: CaveNetwork, var state: ExploreState = ExploreState.UNDETERMINED) : INode {
    init {
        CNNetworkManager.nodeMap[pos] = this
    }

    override fun network(): CaveNetwork = net
    override fun shouldTick(): Boolean = state == ExploreState.UNDETERMINED
    override fun shouldRender(): Boolean = state != ExploreState.MIDDLE
    override fun toString() = "DoorNode:[${net.id}]"

    companion object {
        enum class ExploreState {
            UNDETERMINED,
            MIDDLE,
            EDGE
        }
    }
}
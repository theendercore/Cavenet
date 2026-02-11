package com.theendercore.cavenet.client.network.node

import com.theendercore.cavenet.client.init.CNNetworkManager
import com.theendercore.cavenet.client.network.CaveNetwork
import net.minecraft.core.BlockPos

class DoorNode(pos: BlockPos, val net: CaveNetwork) : INode {
    var isActive = true

    init {
        CNNetworkManager.nodeMap[pos] = this
    }

    override fun network(): CaveNetwork = net
    override fun shouldTick(): Boolean = isActive
    override fun toString() = "DoorNode:[${net.id}]"
}
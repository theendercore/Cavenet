package com.theendercore.cavenet.client.network.node

import com.theendercore.cavenet.client.network.CaveNetwork

interface INode {
    fun shouldRender(): Boolean = true
    fun shouldTick(): Boolean

    //    fun tick(world: ClientLevel, pos: BlockPos)
    fun network(): CaveNetwork
}
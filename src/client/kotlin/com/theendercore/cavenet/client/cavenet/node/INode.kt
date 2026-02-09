package com.theendercore.cavenet.client.cavenet.node

import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.core.BlockPos

interface INode {
    fun shouldRender(): Boolean = true
    fun shouldTick(): Boolean
    fun tick(world: ClientLevel, pos: BlockPos)
}
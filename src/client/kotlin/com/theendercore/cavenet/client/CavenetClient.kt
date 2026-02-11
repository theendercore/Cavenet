package com.theendercore.cavenet.client

import com.theendercore.cavenet.client.init.CNLogic
import com.theendercore.cavenet.client.init.CNRenderer
import com.theendercore.cavenet.client.network.node.INode
import com.theendercore.cavenet.client.init.CNCommands
import net.minecraft.core.BlockPos

@Suppress("unused")
object CavenetClient {
    val nodes = mutableMapOf<BlockPos, INode>()
    fun init() {
        CNLogic.init()
        CNRenderer.init()
        CNCommands.init()
    }
}
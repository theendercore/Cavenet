package com.theendercore.cavenet.client

import com.theendercore.cavenet.client.init.CNCommands
import com.theendercore.cavenet.client.init.CNLogic
import com.theendercore.cavenet.client.init.CNRenderer

object CavenetClient {

    @Suppress("unused")
    fun init() {
        CNLogic.init()
        CNRenderer.init()
        CNCommands.init()
    }
}
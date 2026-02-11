package com.theendercore.cavenet.client

import com.theendercore.cavenet.client.init.CNCommands
import com.theendercore.cavenet.client.init.CNNetworkManager
import com.theendercore.cavenet.client.init.CNRenderer

object CavenetClient {

    @Suppress("unused")
    fun init() {
        CNNetworkManager.init()
        CNRenderer.init()
        CNCommands.init()
    }
}
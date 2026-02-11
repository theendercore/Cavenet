package com.theendercore.cavenet.client

import com.theendercore.cavenet.client.init.CNCommands
import com.theendercore.cavenet.client.init.CNNetworkManager
import com.theendercore.cavenet.client.init.CNRenderer

@Suppress("unused")
object CavenetClient {

    fun init() {
        CNNetworkManager.init()
        CNRenderer.init()
        CNCommands.init()
    }
}
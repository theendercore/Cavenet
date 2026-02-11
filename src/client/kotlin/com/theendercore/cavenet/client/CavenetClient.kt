package com.theendercore.cavenet.client

import com.theendercore.cavenet.client.config.CavenetConfig
import com.theendercore.cavenet.client.init.CNCommands
import com.theendercore.cavenet.client.init.CNNetworkManager
import com.theendercore.cavenet.client.init.CNRenderer
import me.fzzyhmstrs.fzzy_config.api.ConfigApi
import me.fzzyhmstrs.fzzy_config.api.RegisterType

@Suppress("unused")
object CavenetClient {

    @JvmField
    var config = ConfigApi.registerAndLoadConfig(::CavenetConfig, RegisterType.CLIENT)

    fun init() {
        CNNetworkManager.init()
        CNRenderer.init()
        CNCommands.init()
    }
}
package com.theendercore.cavenet.client.config

import com.theendercore.cavenet.Cavenet.MODID
import com.theendercore.cavenet.Cavenet.id
import me.fzzyhmstrs.fzzy_config.config.Config

@Suppress("unused")
class CavenetConfig : Config(id(MODID)) {
    var mainRender = true
    var renderNames = true
    var edgeRender = false
    var tickNetworks = true

    var maxCaveSize = 100_000
    var maxDoorSize = 1_000

}
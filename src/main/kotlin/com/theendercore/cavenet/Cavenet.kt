package com.theendercore.cavenet

import me.fzzyhmstrs.fzzy_config.api.ConfigApi
import net.minecraft.resources.ResourceLocation
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import com.theendercore.cavenet.config.CavenetConfig

@Suppress("unused")
object Cavenet {
    const val MODID = "cavenet"

    @JvmField
    val log: Logger = LoggerFactory.getLogger(Cavenet::class.simpleName)

    @JvmField
    var config = ConfigApi.registerAndLoadConfig(::CavenetConfig)

    fun init() {
        log.info("Hello from Common")
    }

    fun id(namespace: String, path: String): ResourceLocation = ResourceLocation.fromNamespaceAndPath(namespace, path)
    fun mc(path: String): ResourceLocation = ResourceLocation.withDefaultNamespace(path)
    fun id(path: String) = id(MODID, path)
}

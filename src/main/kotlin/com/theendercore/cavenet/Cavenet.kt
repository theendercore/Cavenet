package com.theendercore.cavenet

import net.minecraft.resources.ResourceLocation
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Suppress("unused")
object Cavenet {
    const val MODID = "cavenet"

    @JvmField
    val log: Logger = LoggerFactory.getLogger(Cavenet::class.simpleName)

    fun init() {
        log.info("Hello from Common")
    }

    fun id(namespace: String, path: String): ResourceLocation = ResourceLocation.fromNamespaceAndPath(namespace, path)
    fun mc(path: String): ResourceLocation = ResourceLocation.withDefaultNamespace(path)
    fun id(path: String) = id(MODID, path)
}

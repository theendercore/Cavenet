package com.theendercore.cavenet.client.init

import com.theendercore.cavenet.client.sendMessage
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.KeyMapping
import org.lwjgl.glfw.GLFW

object CNKeybinds {
    var group = "Cavenet"
    val addKey: KeyMapping =
        KeyBindingHelper.registerKeyBinding(KeyMapping("Add Network", GLFW.GLFW_KEY_UNKNOWN, group))

    var cooldown = 0

    fun init() {
        ClientTickEvents.END_CLIENT_TICK.register {
            if (addKey.consumeClick()) {
                it.player?.let { p ->
                    val net = CNNetworkManager.addNetwork(p)
                    sendMessage("Crated network ${net}!")
                    cooldown = 30
                }
            }
            if (cooldown > 0) cooldown--
        }
    }

}
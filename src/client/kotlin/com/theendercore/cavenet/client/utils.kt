package com.theendercore.cavenet.client

import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component

fun sendMessage(msg: String) {
    Minecraft.getInstance().player?.sendSystemMessage(Component.literal(msg))
}
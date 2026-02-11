package com.theendercore.cavenet.client.init

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.context.CommandContext
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.network.chat.Component

object CNCommands {
    fun init() {
        ClientCommandRegistrationCallback.EVENT.register { dispatcher, _ ->
            val root = literal("cavenet").build()
            dispatcher.root.addChild(root)

            val create = literal("create").executes(::create).build()
            root.addChild(create)

            val clear = literal("clear").executes(::clearNodes).build()
            root.addChild(clear)

            val ticks = literal("ticks").executes {
                it.source.sendFeedback(Component.translatable("Current tick rate is: ${CNLogic.ticksToSkip}"))
                1
            }.build()
            root.addChild(ticks)

            val ticksArg = argument("ticks", IntegerArgumentType.integer(-1)).executes {
                CNLogic.ticksToSkip = IntegerArgumentType.getInteger(it, "ticks")
                it.source.sendFeedback(Component.translatable("Tick rate set to: ${CNLogic.ticksToSkip}"))

                0
            }.build()
            ticks.addChild(ticksArg)
        }

    }

    fun create(ctx: CommandContext<FabricClientCommandSource>): Int {
        val src = ctx.source ?: return -1
        val player = src.player ?: return -1

        val net = CNLogic.addNetwork(player)
        src.sendFeedback(Component.translatable("Crated network ${net}!"))
        return Command.SINGLE_SUCCESS
    }

    fun clearNodes(ctx: CommandContext<FabricClientCommandSource>): Int {
        val src = ctx.source ?: return -1

        val netCount = CNLogic.networks.size
        val nodeCount = CNLogic.nodeMap.size
        CNLogic.nodeMap.clear()
        CNLogic.networks.clear()
        src.sendFeedback(Component.literal("Cleared $netCount networks and $nodeCount nodes!"))
        return Command.SINGLE_SUCCESS
    }
}
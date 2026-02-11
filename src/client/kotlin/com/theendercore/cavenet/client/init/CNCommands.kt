package com.theendercore.cavenet.client.init

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType.string
import com.mojang.brigadier.arguments.StringArgumentType.getString
import com.mojang.brigadier.context.CommandContext
import com.theendercore.cavenet.client.network.CaveNetwork
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.network.chat.Component
import org.teamvoided.creative_works.comands.utils.ImprovedLookup.listSuggestions

object CNCommands {
    fun init() = ClientCommandRegistrationCallback.EVENT.register { dispatcher, _ ->
        val root = literal("cavenet").build()
        dispatcher.root.addChild(root)

        val create = literal("create").executes(::create).build()
        root.addChild(create)

        val clear = literal("clear").executes(::clearNodes).build()
        root.addChild(clear)

        val ticks = literal("ticks").executes {
            it.source.sendFeedback(Component.translatable("Current tick rate is: ${CNNetworkManager.ticksToSkip}"))
            1
        }.build()
        root.addChild(ticks)

        val ticksArg = argument("ticks", IntegerArgumentType.integer(-1)).executes {
            CNNetworkManager.ticksToSkip = IntegerArgumentType.getInteger(it, "ticks")
            it.source.sendFeedback(Component.translatable("Tick rate set to: ${CNNetworkManager.ticksToSkip}"))

            0
        }.build()
        ticks.addChild(ticksArg)


        val freeze = literal("freeze").then(
            argument("id", string())
                .suggests { _, builder -> builder.listSuggestions(CNNetworkManager.networks.map { it.id.toString() }) }
                .executes { ctx ->
                    val id = getString(ctx, "id")
                    val net = CNNetworkManager.networks.firstOrNull { it.id.toString() == id }
                    if (net != null) {
                        net.phase = CaveNetwork.NetPhase.FROZEN
                        ctx.source.sendFeedback(Component.translatable("Froze network: $id"))
                        1
                    } else {
                        ctx.source.sendError(Component.translatable("No network with id: $id"))
                        0
                    }
                }
        ).build()
        root.addChild(freeze)


        val unfreeze = literal("unfreeze").then(
            argument("id", string())
                .suggests { _, builder -> builder.listSuggestions(CNNetworkManager.networks.map { it.id.toString() }) }
                .executes { ctx ->
                    val id = getString(ctx, "id")
                    val net = CNNetworkManager.networks.firstOrNull { it.id.toString() == id }
                    if (net != null) {
                        net.phase = CaveNetwork.NetPhase.OPENING_DOOR
                        ctx.source.sendFeedback(Component.translatable("Unfroze network: $id"))
                        1
                    } else {
                        ctx.source.sendError(Component.translatable("No network with id: $id"))
                        0
                    }
                }
        ).build()
        root.addChild(unfreeze)


        val freezeAll = literal("freeze_all").executes { ctx ->
            for (net in CNNetworkManager.networks) {
                net.phase = CaveNetwork.NetPhase.FROZEN
            }
            ctx.source.sendFeedback(Component.translatable("Froze ${CNNetworkManager.networks} networks"))
            0
        }.build()
        root.addChild(freezeAll)

        val unfreezeAll = literal("unfreeze_all").executes { ctx ->
            for (net in CNNetworkManager.networks) {
                net.phase = CaveNetwork.NetPhase.OPENING_DOOR
            }
            ctx.source.sendFeedback(Component.translatable("Unfroze ${CNNetworkManager.networks} networks"))
            0
        }.build()
        root.addChild(unfreezeAll)


        val delete = literal("delete").then(
            argument("id", string())
                .suggests { _, builder -> builder.listSuggestions(CNNetworkManager.networks.map { it.id.toString() }) }
                .executes { ctx ->
                    val id = getString(ctx, "id")
                    val net = CNNetworkManager.networks.firstOrNull { it.id.toString() == id }
                    if (net != null) {
                        CNNetworkManager.removeNetwork(net)
                        ctx.source.sendFeedback(Component.translatable("Deleted network: $id"))
                        1
                    } else {
                        ctx.source.sendError(Component.translatable("No network with id: $id"))
                        0
                    }
                }
        ).build()
        root.addChild(delete)
    }

    fun create(ctx: CommandContext<FabricClientCommandSource>): Int {
        val src = ctx.source ?: return -1
        val player = src.player ?: return -1

        val net = CNNetworkManager.addNetwork(player)
        src.sendFeedback(Component.translatable("Crated network ${net}!"))
        return Command.SINGLE_SUCCESS
    }

    fun clearNodes(ctx: CommandContext<FabricClientCommandSource>): Int {
        val src = ctx.source ?: return -1

        val netCount = CNNetworkManager.networks.size
        val nodeCount = CNNetworkManager.nodeMap.size
        CNNetworkManager.nodeMap.clear()
        CNNetworkManager.networks.clear()
        src.sendFeedback(Component.literal("Cleared $netCount networks and $nodeCount nodes!"))
        return Command.SINGLE_SUCCESS
    }
}
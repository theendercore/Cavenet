package com.theendercore.cavenet.client.init

import com.theendercore.cavenet.client.CavenetClient
import com.theendercore.cavenet.client.network.CaveNetwork
import com.theendercore.cavenet.client.network.node.INode
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.tags.FluidTags
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.block.state.BlockState
import java.util.*

object CNNetworkManager {
    val networks = mutableListOf<CaveNetwork>()
    val nodeMap = mutableMapOf<BlockPos, INode>()
    fun addNetwork(player: Player) = addNetwork(
        BlockPos(player.blockX, player.eyePosition.y.toInt(), player.blockZ),
        player.nearestViewDirection
    )

    fun addNetwork(pos: BlockPos, dir: Direction): CaveNetwork {
        val net = CaveNetwork(pos, dir)
        networks.add(net)
        return net
    }

    fun removeNetwork(net: CaveNetwork) {
        net.clear()
        networks.remove(net)
    }

    val toRemove = mutableListOf<UUID>()

    var ticksToSkip = -1
    var operationsPerTick = -1
    var skippedTickCounter = 0

    fun init() = ClientTickEvents.END_WORLD_TICK.register(::clientTick)


    fun clientTick(world: ClientLevel) {
        if (!CavenetClient.config.tickNetworks) return

        if (networks.isEmpty()) {
            if (nodeMap.isNotEmpty()) {
                println("No networks exits but node map not empty! Clearing all nodes!")
                nodeMap.clear()
                networks.clear()
            }
            return
        }


        for (net in networks) {
            if (toRemove.contains(net.id)) continue
            net.tick(world)
        }

        for (id in toRemove) {
            removeNetwork(networks.first { it.id == id })
        }
        toRemove.clear()

    }

    fun canNodeExplore(world: ClientLevel, pos: BlockPos): Boolean {
        return canBeInBlock(world, pos, world.getBlockState(pos)) && !world.canSeeSky(pos)
    }

    fun canBeInBlock(world: ClientLevel, pos: BlockPos, state: BlockState): Boolean {
        if (state.isAir) return true

        return state.getCollisionShape(world, pos).isEmpty && !state.fluidState.`is`(FluidTags.LAVA)
    }

}

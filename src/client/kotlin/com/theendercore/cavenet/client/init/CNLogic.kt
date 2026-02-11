package com.theendercore.cavenet.client.init

import com.theendercore.cavenet.client.network.CaveNetwork
import com.theendercore.cavenet.client.network.node.INode
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.entity.player.Player

object CNLogic {
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

    var ticksToSkip = -1
    var operationsPerTick = -1
    var skippedTickCounter = 0

    fun init() = ClientTickEvents.END_WORLD_TICK.register(::clientTick)


    fun clientTick(world: ClientLevel) {
        if (networks.isEmpty()) {
            if (nodeMap.isNotEmpty()) {
                println("No networks exits but node map not empty! Clearing all nodes!")
                nodeMap.clear()
                networks.clear()
            }
            return
        }


        for (net in networks) {
            net.tick(world)
        }

    }

    fun canNodeExplore(world: ClientLevel, pos: BlockPos): Boolean {
        return world.getBlockState(pos).isAir && !world.canSeeSky(pos)
    }

}

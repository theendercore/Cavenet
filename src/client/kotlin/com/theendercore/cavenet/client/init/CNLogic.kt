package com.theendercore.cavenet.client.init

import com.theendercore.cavenet.client.CavenetClient.nodes
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
    fun addNetwork(player: Player) = addNetwork(player.blockPosition(), player.nearestViewDirection)
    fun addNetwork(pos: BlockPos, dir: Direction): CaveNetwork {
        val net = CaveNetwork(pos, dir)
        networks.add(net)
        return net
    }

    fun removeNetwork(net: CaveNetwork) {
        net.clear()
        networks.remove(net)
    }

    var TicksPerTick = -1
    var tickCounter = 0

    fun init() = ClientTickEvents.END_WORLD_TICK.register(::clientTick)


    fun clientTick(world: ClientLevel) {
        if (nodes.isEmpty()) return
        var ticks = true
        if (TicksPerTick > -1) {
            if (TicksPerTick == 0) ticks = false
            else {
                tickCounter++
                if (tickCounter >= TicksPerTick) tickCounter = 0
                else ticks = false
            }
        }

        for ((pos, node) in nodes.toList()) {
            if (ticks && node.shouldTick()) {
                node.tick(world, pos)
            }
        }

    }

    fun canNodeExplore(world: ClientLevel, pos: BlockPos): Boolean {
        return world.getBlockState(pos).isAir && !world.canSeeSky(pos)
    }

}

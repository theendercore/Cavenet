package com.theendercore.cavenet.client.cavenet

import com.theendercore.cavenet.client.CavenetClient.TicksPerTick
import com.theendercore.cavenet.client.CavenetClient.nodes
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.core.BlockPos

object CNLogic {
    fun init() {
        ClientTickEvents.END_WORLD_TICK.register(::clientTick)
    }

    fun canNodeExplore(world: ClientLevel, pos: BlockPos): Boolean {
        return world.getBlockState(pos).isAir && !world.canSeeSky(pos)
    }

    var tickCounter = 0

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
}

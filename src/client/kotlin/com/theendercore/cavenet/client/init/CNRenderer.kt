package com.theendercore.cavenet.client.init

import com.theendercore.cavenet.Cavenet.mc
import com.theendercore.cavenet.client.network.CaveNetwork
import com.theendercore.cavenet.client.rendering.drawFaceFromDir
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import net.minecraft.client.Camera
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.LevelRenderer.getLightColor
import net.minecraft.client.renderer.RenderType
import net.minecraft.core.particles.DustParticleOptions
import net.minecraft.util.Mth
import net.minecraft.world.phys.Vec3
import kotlin.math.max

object CNRenderer {
    val GLASS = mc("textures/block/blue_stained_glass.png")

    fun init() = WorldRenderEvents.AFTER_TRANSLUCENT.register(::renderCustom)

    fun renderCustom(ctx: WorldRenderContext) {
        if (CNLogic.networks.isEmpty()) return
        val posStack = ctx.matrixStack() ?: return
        val consumers = ctx.consumers() ?: return
        val profiler = ctx.profiler()
        profiler.push("Cavenet")

        val world = ctx.world()
        posStack.pushPose()

        val mtx = posStack.last().pose()
        val buffer = consumers.getBuffer(RenderType.entityTranslucent(GLASS))!!
        val camPos = ctx.camera().position

        val color = 0xff_ffffff.toInt()

        for (net in CNLogic.networks) {
            if (!ctx.camera().isInRenderDistance(net)) continue

            world.addParticle(
                DustParticleOptions(Vec3.fromRGB24(0xff0000).toVector3f(), 1.0F),
                net.pos.x + 0.5, net.pos.y + 0.5, net.pos.z + 0.5, 0.0, 0.0, 0.0
            )

            if (net.isEmpty()) continue
            for ((nodePos, node) in net.nodes()) {
                if (!node.shouldRender()) continue

                val light = max(getLightColor(world, nodePos), 7 shl 4)
                buffer.drawFaceFromDir(mtx, nodePos, camPos, color, light, node.network().direction)
            }
        }

        posStack.popPose()
        profiler.pop()
    }

    fun Camera.isInRenderDistance(net: CaveNetwork): Boolean {
        val x = position.x - net.pos.x
        val y = position.y - net.pos.y
        val z = position.z - net.pos.z

        return (x * x + y * y + z * z) <= Mth.square(Minecraft.getInstance().options.effectiveRenderDistance * 16.0)
    }
}


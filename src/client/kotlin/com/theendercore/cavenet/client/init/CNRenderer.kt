package com.theendercore.cavenet.client.init

import com.mojang.blaze3d.vertex.VertexConsumer
import com.theendercore.cavenet.Cavenet.mc
import com.theendercore.cavenet.client.CavenetClient.nodes
import com.theendercore.cavenet.client.network.node.DoorNode
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import net.minecraft.client.renderer.LevelRenderer.getLightColor
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.phys.Vec3
import org.joml.Matrix4f
import kotlin.math.max

object CNRenderer {
    val GLASS = mc("textures/block/blue_stained_glass.png")

    fun init() = WorldRenderEvents.AFTER_TRANSLUCENT.register(::renderCustom)

    fun renderCustom(ctx: WorldRenderContext) {
        val posStack = ctx.matrixStack() ?: return
        val profiler = ctx.profiler()
        profiler.push("Cavenet")

        val world = ctx.world()
        posStack.pushPose()
        if (nodes.isNotEmpty()) {

            val mtx = posStack.last().pose()
            val buffer = ctx.consumers()!!.getBuffer(RenderType.entityTranslucent(GLASS))
            val camPos = ctx.camera().position

            val color = 0xff_ff_ff_ff.toInt()

            for ((nodePos, node) in nodes.toList()) {
                if (node.shouldRender() && node is DoorNode) {
                    val light = max(getLightColor(world, nodePos), 7 shl 4)
                    buffer.faceFromDir(mtx, nodePos, camPos, color, light, node.network().direction)
                }
            }
        }

        posStack.popPose()
        profiler.pop()
    }

    fun VertexConsumer.faceFromDir(
        mtx: Matrix4f,
        nodePos: BlockPos,
        camPos: Vec3,
        color: Int,
        light: Int,
        dir: Direction,
    ) {
        val x1 = nodePos.x + (dir.normal.x * 0.5)
        val y1 = nodePos.y
        val z1 = nodePos.z + (dir.normal.z * 0.5)

        val x2 = x1 + 1
        val y2 = y1 + 1
        val z2 = z1 + 1
        when (dir) {
            Direction.DOWN -> {}
            Direction.UP -> {}
            Direction.SOUTH,
                -> face(mtx, x2, y2, z1, x1, y1, z1, camPos, color, light, vec3(0, 1, 0))

            Direction.NORTH,
                -> face(mtx, x1, y2, z2, x2, y1, z2, camPos, color, light, vec3(0, 1, 0))

            Direction.EAST,
                -> face(mtx, x1, y2, z1, x1, y1, z2, camPos, color, light, vec3(0, 1, 0))

            Direction.WEST,
                -> face(mtx, x2, y2, z2, x2, y1, z1, camPos, color, light, vec3(0, 1, 0))
        }
    }

    fun VertexConsumer.face(
        mtx: Matrix4f, x1: Number, y1: Number, z1: Number, x2: Number, y2: Number, z2: Number,
        camPos: Vec3, color: Int, light: Int, normal: Vec3,
    ) {

        addVertex(mtx, vec3(x1, y1, z1), camPos)
            .setColor(color)
            .setUv(0.0F, 0.0F)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setLight(light)
            .normal(normal)

        addVertex(mtx, vec3(x1, y2, z1), camPos)
            .setColor(color)
            .setUv(1.0F, 0.0F)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setLight(light)
            .normal(normal)

        addVertex(mtx, vec3(x2, y2, z2), camPos)
            .setColor(color)
            .setUv(1.0F, 1.0F)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setLight(light)
            .normal(normal)

        addVertex(mtx, vec3(x2, y1, z2), camPos)
            .setColor(color)
            .setUv(0.0F, 1.0F)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setLight(light)
            .normal(normal)
    }

    fun VertexConsumer.addVertex(model: Matrix4f, vec: Vec3, camera: Vec3): VertexConsumer =
        addVertex(model, (vec.x - camera.x).toFloat(), (vec.y - camera.y).toFloat(), (vec.z - camera.z).toFloat())

    fun VertexConsumer.normal(vec: Vec3): VertexConsumer =
        setNormal(vec.x.toFloat(), vec.y.toFloat(), vec.z.toFloat())

    fun vec3(x: Number, y: Number, z: Number) = Vec3(x.toDouble(), y.toDouble(), z.toDouble())
}


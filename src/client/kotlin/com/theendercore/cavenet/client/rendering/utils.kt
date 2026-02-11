package com.theendercore.cavenet.client.rendering

import com.mojang.blaze3d.vertex.VertexConsumer
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.phys.Vec3
import org.joml.Matrix4f


fun VertexConsumer.drawFaceFromDir(
    mtx: Matrix4f,
    nodePos: BlockPos,
    camPos: Vec3,
    color: Int,
    light: Int,
    dir: Direction,
    sprite: TextureAtlasSprite,
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
            -> face(mtx, x2, y2, z1, x1, y1, z1, camPos, color, light, vec3(0, 1, 0), sprite)

        Direction.NORTH,
            -> face(mtx, x1, y2, z2, x2, y1, z2, camPos, color, light, vec3(0, 1, 0), sprite)

        Direction.EAST,
            -> face(mtx, x1, y2, z1, x1, y1, z2, camPos, color, light, vec3(0, 1, 0), sprite)

        Direction.WEST,
            -> face(mtx, x2, y2, z2, x2, y1, z1, camPos, color, light, vec3(0, 1, 0), sprite)
    }
}

fun VertexConsumer.face(
    mtx: Matrix4f, x1: Number, y1: Number, z1: Number, x2: Number, y2: Number, z2: Number,
    camPos: Vec3, color: Int, light: Int, normal: Vec3, sprite: TextureAtlasSprite,
) {

    addVertex(mtx, vec3(x1, y1, z1), camPos)
        .setColor(color)
        .setUv(sprite.u0, sprite.v0)
        .setOverlay(OverlayTexture.NO_OVERLAY)
        .setLight(light)
        .normal(normal)

    addVertex(mtx, vec3(x1, y2, z1), camPos)
        .setColor(color)
        .setUv(sprite.u0, sprite.v1)
        .setOverlay(OverlayTexture.NO_OVERLAY)
        .setLight(light)
        .normal(normal)

    addVertex(mtx, vec3(x2, y2, z2), camPos)
        .setColor(color)
        .setUv(sprite.u1, sprite.v1)
        .setOverlay(OverlayTexture.NO_OVERLAY)
        .setLight(light)
        .normal(normal)

    addVertex(mtx, vec3(x2, y1, z2), camPos)
        .setColor(color)
        .setUv(sprite.u1, sprite.v0)
        .setOverlay(OverlayTexture.NO_OVERLAY)
        .setLight(light)
        .normal(normal)
}

fun VertexConsumer.addVertex(model: Matrix4f, vec: Vec3, camera: Vec3): VertexConsumer =
    addVertex(model, (vec.x - camera.x).toFloat(), (vec.y - camera.y).toFloat(), (vec.z - camera.z).toFloat())
fun VertexConsumer.normal(vec: Vec3): VertexConsumer = setNormal(vec.x.toFloat(), vec.y.toFloat(), vec.z.toFloat())
fun vec3(x: Number, y: Number, z: Number) = Vec3(x.toDouble(), y.toDouble(), z.toDouble())


package com.theendercore.cavenet.client.init

import com.theendercore.cavenet.Cavenet.id
import com.theendercore.cavenet.Cavenet.mc
import com.theendercore.cavenet.client.CavenetClient
import com.theendercore.cavenet.client.network.CaveNetwork
import com.theendercore.cavenet.client.network.node.DoorNode
import com.theendercore.cavenet.client.network.node.ExploreNode
import com.theendercore.cavenet.client.network.node.ExploreNode.Companion.ExploreState
import com.theendercore.cavenet.client.rendering.drawDoorFace
import com.theendercore.cavenet.client.rendering.drawFaceFromDir
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import net.minecraft.client.Camera
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.LevelRenderer.getLightColor
import net.minecraft.client.renderer.RenderType
import net.minecraft.util.Mth
import net.minecraft.world.inventory.InventoryMenu
import kotlin.math.max

object CNRenderer {
    val DONE = mc("block/blue_stained_glass")
    val OPEN = mc("block/orange_stained_glass")
    val EXPLORE = mc("block/green_stained_glass")
    val FROZEN = mc("block/ice")

    fun init() = WorldRenderEvents.AFTER_TRANSLUCENT.register(::renderCustom)

    fun renderCustom(ctx: WorldRenderContext) {
        if (!CavenetClient.config.mainRender) return
        if (CNNetworkManager.networks.isEmpty()) return
        val posStack = ctx.matrixStack() ?: return
        val consumers = ctx.consumers() ?: return
        val profiler = ctx.profiler()
        profiler.push("Cavenet")

        val world = ctx.world()
        posStack.pushPose()

        val mtx = posStack.last().pose()
        val buffer = consumers.getBuffer(RenderType.entityTranslucent(InventoryMenu.BLOCK_ATLAS))
        // Sprites
        val atlas = ctx.gameRenderer().minecraft.getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
        val open = atlas.apply(OPEN)
        val explore = atlas.apply(EXPLORE)
        val done = atlas.apply(DONE)
        val frozen = atlas.apply(FROZEN)

        val redstone = atlas.apply(mc("item/redstone"))
        val edge = atlas.apply(id("block/edge"))
        val undetermined = atlas.apply(id("block/undetermined"))

        val camPos = ctx.camera().position

        val color = 0xff_ffffff.toInt()

        for (net in CNNetworkManager.networks) {
            if (!ctx.camera().isInRenderDistance(net)) continue

            buffer.drawDoorFace(mtx, net.pos, camPos, color, 14 shl 4, net.direction, redstone)
            /*  if (CavenetClient.config.renderNames) {
                  posStack.pushPose()
                  posStack.translate(0f, .5f, 0f)
                  posStack.scale(0.025f, -0.025f, 0.025f)

                  val color = 0xff_ff_ff_ff.toInt()
                  val font = Minecraft.getInstance().font
                  val text = Component.literal(net.id.toString())
                  font.drawInBatch(
                      text, font.width(text) / -2f, 0f, color,
                      true, mtx, consumers,
                      Font.DisplayMode.NORMAL, 0, 15728880
                  )
                  posStack.popPose()
              }*/

            if (net.isEmpty()) continue
            for (nodePos in net.nodePositions()) {
                val node = CNNetworkManager.nodeMap[nodePos] ?: continue
                if (!node.shouldRender()) continue
                val light = max(getLightColor(world, nodePos), 7 shl 4)

                if (node is DoorNode) {
                    val sprite = when (net.phase) {
                        CaveNetwork.NetPhase.OPENING_DOOR -> open
                        CaveNetwork.NetPhase.EXPLORING_CAVE -> explore
                        CaveNetwork.NetPhase.COMPLETE -> done
                        CaveNetwork.NetPhase.FROZEN -> frozen
                    }

                    buffer.drawDoorFace(mtx, nodePos, camPos, color, light, net.direction, sprite)
                } else if (node is ExploreNode && CavenetClient.config.edgeRender) {
                    if (node.state == ExploreState.EDGE) {
                        for (direction in node.edges) {
                            buffer.drawFaceFromDir(mtx, nodePos, camPos, color, light, direction.opposite, edge)
                        }
                        if (node.edges.isEmpty()) {
                            buffer.drawDoorFace(mtx, nodePos, camPos, color, light, net.direction, edge)
                        }
                    } else {
                        buffer.drawDoorFace(mtx, nodePos, camPos, color, light, net.direction, undetermined)
                    }
                }
            }
        }

        posStack.popPose()
        profiler.pop()
    }

    fun Camera.isInRenderDistance(net: CaveNetwork): Boolean {
        val x = position.x - net.pos.x
        val y = position.y - net.pos.y
        val z = position.z - net.pos.z

        return (x * x + y * y + z * z) <= Mth.square(Minecraft.getInstance().options.effectiveRenderDistance * 16)
    }
}


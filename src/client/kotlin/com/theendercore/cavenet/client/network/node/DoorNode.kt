package com.theendercore.cavenet.client.network.node

import com.theendercore.cavenet.client.network.CaveNetwork
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.core.BlockPos

class DoorNode(val net: CaveNetwork) : INode {
    var isActive = true

    override fun shouldTick(): Boolean = isActive
    override fun network(): CaveNetwork = net

   /* fun tick(world: ClientLevel, pos: BlockPos) {
          if (!CNLogic.canNodeExplore(world, pos)) {
              nodes.remove(pos)
              return
          }

          for (dir in Direction.entries) {
              if (dir == direction || dir == direction.opposite) continue

              val sidePos = pos.relative(dir)
              if (!CNLogic.canNodeExplore(world, sidePos)) continue
              if (nodes.contains(sidePos)) continue

              nodes[sidePos] = DoorNode(direction)
              return
          }

          // add ExploreNode
          val pointPos = pos.relative(direction)
          if (!CNLogic.canNodeExplore(world, pointPos)) return
          if (nodes.contains(pointPos)) return
          nodes[pointPos] = ExploreNode()
          isActive = false
    }*/
}
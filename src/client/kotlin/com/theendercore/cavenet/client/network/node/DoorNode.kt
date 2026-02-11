package com.theendercore.cavenet.client.network.node

import com.theendercore.cavenet.client.init.CNLogic
import com.theendercore.cavenet.client.network.CaveNetwork
import net.minecraft.core.BlockPos

class DoorNode(pos: BlockPos, val net: CaveNetwork) : INode {
    var isActive = true

    init {
        CNLogic.nodeMap[pos] = this
    }

    override fun shouldTick(): Boolean = isActive
    override fun network(): CaveNetwork = net

    override fun toString() = "DoorNode:[$net]"

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
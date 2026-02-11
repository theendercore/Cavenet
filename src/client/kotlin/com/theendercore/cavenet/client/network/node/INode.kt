package com.theendercore.cavenet.client.network.node

import com.theendercore.cavenet.client.network.CaveNetwork

interface INode {
    fun shouldRender(): Boolean = true
    fun shouldTick(): Boolean
    fun network(): CaveNetwork
}
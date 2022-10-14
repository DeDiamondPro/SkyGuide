package dev.dediamondpro.skyguide.map.navigation

import dev.dediamondpro.skyguide.map.Island

data class Destination(val island: Island, val x: Float, val y: Float?, val z: Float, val name: String = "your destination")
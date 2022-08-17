package cc.polyfrost.polyblock.map

import cc.polyfrost.oneconfig.renderer.Image
import cc.polyfrost.polyblock.utils.MapPart

object SkyblockMap {
    val mapParts = mutableListOf(MapPart("hub", Image("/assets/polyblock/hub.png"), -298f, -258f, 201f, 241f))
    val topX: Float
    val topY: Float
    val bottomX: Float
    val bottomY: Float

    init {
        var topX = mapParts[0].topX
        var topY = mapParts[0].topY
        var bottomX = mapParts[0].bottomX
        var bottomY = mapParts[0].bottomY
        for (mapPart in mapParts) {
            topX = topX.coerceAtMost(mapPart.topX)
            topY = topY.coerceAtMost(mapPart.topY)
            bottomX = bottomX.coerceAtLeast(mapPart.bottomX)
            bottomY = bottomY.coerceAtLeast(mapPart.bottomY)
        }
        this.topX = topX
        this.topY = topY
        this.bottomX = bottomX
        this.bottomY = bottomY
    }
}
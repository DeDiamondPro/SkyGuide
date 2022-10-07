package dev.dediamondpro.skyguide.map

import dev.dediamondpro.skyguide.config.Config
import dev.dediamondpro.skyguide.utils.RenderUtils

/**
 * @param image The image of the map
 * @param topX The top left X coordinate in mc
 * @param topY The top left Y coordinate in mc
 * @param bottomX The bottom right X coordinate in mc
 * @param bottomY The bottom right Y coordinate in mc
 */
@kotlinx.serialization.Serializable
data class Island(
    var images: MutableMap<Int, Textures>,
    val topX: Float,
    val topY: Float,
    val bottomX: Float,
    val bottomY: Float,
    val xOffset: Float = 0f, // The offset of the player coordinates
    val yOffset: Float = 0f
) {
    val width = bottomX - topX
    val height = bottomY - topY
    var zone: String? = null
        private set

    init {
        images = images.toSortedMap()
    }

    fun draw(y: Int) {
        getImage(y).draw(topX + xOffset, topY + yOffset, width, height)
        if (zone == null) zone = SkyblockMap.getZoneByIsland(this)
        for (waypoint in Config.waypoints) {
            if (waypoint.zone != zone) continue
            RenderUtils.drawRect(waypoint.x - 2, waypoint.y - 2, 4, 4, waypoint.color)
        }
    }

    fun getImage(y: Int): Textures {
        for ((height, image) in images) {
            if (height >= y) return image
        }
        return images[0]!!
    }

    fun isInIsland(x: Float, y: Float): Boolean {
        return x >= topX + xOffset && y >= topY + yOffset
                && x <= bottomX + xOffset && y <= bottomY + yOffset
    }

    companion object {
        fun getXOffset(): Float {
            return SkyblockMap.getCurrentIsland()?.xOffset ?: 0f
        }

        fun getYOffset(): Float {
            return SkyblockMap.getCurrentIsland()?.yOffset ?: 0f
        }
    }
}

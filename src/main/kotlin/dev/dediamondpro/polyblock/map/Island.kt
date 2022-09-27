package dev.dediamondpro.polyblock.map

import cc.polyfrost.oneconfig.utils.dsl.drawCircle
import cc.polyfrost.oneconfig.utils.dsl.nanoVG
import dev.dediamondpro.polyblock.config.BlockConfig

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

    fun draw(vg: Long, y: Int) {
        getImage(y).draw(vg, topX + xOffset, topY + yOffset, width, height)
        if (zone == null) zone = SkyblockMap.getZoneByIsland(this)
        nanoVG(vg) {
            for (waypoint in BlockConfig.waypoints) {
                if (waypoint.zone != zone) continue
                drawCircle(waypoint.x, waypoint.y, 3, waypoint.color)
            }
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

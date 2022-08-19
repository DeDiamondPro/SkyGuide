package cc.polyfrost.polyblock.map

import cc.polyfrost.oneconfig.utils.dsl.drawImage
import cc.polyfrost.oneconfig.utils.dsl.nanoVG
import cc.polyfrost.polyblock.utils.AssetHandler
import cc.polyfrost.polyblock.utils.SBInfo

/**
 * @param image The image of the map
 * @param topX The top left X coordinate in mc
 * @param topY The top left Y coordinate in mc
 * @param bottomX The bottom right X coordinate in mc
 * @param bottomY The bottom right Y coordinate in mc
 */
@kotlinx.serialization.Serializable
data class Island(
    val image: Textures,
    val topX: Float,
    val topY: Float,
    val bottomX: Float,
    val bottomY: Float,
    val xOffset: Float = 0f, // The offset of the player coordinates
    val yOffset: Float = 0f
) {
    val width = bottomX - topX
    val height = bottomY - topY

    fun draw(vg: Long) {
        image.draw(vg, topX + xOffset, topY + yOffset, width, height)
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

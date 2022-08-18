package cc.polyfrost.polyblock.utils

import cc.polyfrost.oneconfig.utils.dsl.drawImage
import cc.polyfrost.oneconfig.utils.dsl.nanoVG
import cc.polyfrost.polyblock.map.SkyblockMap

/**
 * @param image The image of the map
 * @param topX The top left X coordinate in mc
 * @param topY The top left Y coordinate in mc
 * @param bottomX The bottom right X coordinate in mc
 * @param bottomY The bottom right Y coordinate in mc
 */
data class Island(
    val image: String,
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
        nanoVG(vg) {
            AssetHandler.loadAsset(vg, image)
            drawImage(image, topX + xOffset, topY + yOffset, width, height)
        }
    }

    companion object {
        fun getXOffset(): Float {
            return SkyblockMap.islands[SBInfo.zone]?.xOffset ?: 0f
        }

        fun getYOffset(): Float {
            return SkyblockMap.islands[SBInfo.zone]?.yOffset ?: 0f
        }
    }
}

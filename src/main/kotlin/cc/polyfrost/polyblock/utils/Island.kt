package cc.polyfrost.polyblock.utils

import cc.polyfrost.oneconfig.renderer.RenderManager

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
    val bottomY: Float
) {
    val width = bottomX - topX
    val height = bottomY - topY

    fun draw(vg: Long) {
        RenderManager.drawImage(vg, image, topX, topY, width, height)
    }
}

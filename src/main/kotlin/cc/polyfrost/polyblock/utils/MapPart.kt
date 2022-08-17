package cc.polyfrost.polyblock.utils

import cc.polyfrost.oneconfig.renderer.Image
import cc.polyfrost.oneconfig.renderer.RenderManager

/**
 * @param id ID of the map, same as locraw data
 * @param image The image of the map
 * @param topX The top left X coordinate in mc
 * @param topY The top left Y coordinate in mc
 * @param bottomX The bottom right X coordinate in mc
 * @param bottomY The bottom right Y coordinate in mc
 */
data class MapPart(val id: String, val image: Image, val topX: Float, val topY: Float, val bottomX: Float, val bottomY: Float) {
    private val width = bottomX - topX
    private val height = bottomY - topY

    fun draw(vg: Long, x: Float, y: Float) {
        RenderManager.drawImage(vg, image, topX - x, topY - y, width, height)
    }
}

package dev.dediamondpro.skyguide.map

import dev.dediamondpro.skyguide.map.navigation.NavigationHandler
import dev.dediamondpro.skyguide.map.poi.DestinationPoi
import dev.dediamondpro.skyguide.map.poi.PointOfInterest
import dev.dediamondpro.skyguide.map.poi.Portal
import dev.dediamondpro.skyguide.utils.GuiUtils

/**
 * @param images The images of the map
 * @param portals The portals on the map
 * @param topX The top left X coordinate in mc
 * @param topY The top left Y coordinate in mc
 * @param bottomX The bottom right X coordinate in mc
 * @param bottomY The bottom right Y coordinate in mc
 * @param xOffset The X offset of the player coordinates
 * @param yOffset The Y offset of the player coordinates
 */
@kotlinx.serialization.Serializable
data class Island(
    var images: MutableMap<Int, Textures>,
    val portals: MutableList<Portal> = mutableListOf(),
    val name: String,
    val topX: Float,
    val topY: Float,
    val bottomX: Float,
    val bottomY: Float,
    val xOffset: Float = 0f,
    val yOffset: Float = 0f
) {
    val width = bottomX - topX
    val height = bottomY - topY
    var zone: String? = null
        private set
        get() {
            if (field == null) field = SkyblockMap.getZoneByIsland(this)
            return field
        }

    init {
        images = images.toSortedMap()
        for (poi in getPointsOfInterest()) poi.island = this
    }

    fun draw(y: Int) {
        getImage(y).draw(topX + xOffset, topY + yOffset, width, height)
    }

    fun drawLast(scale: Float, locations: MutableList<Pair<Float, Float>>) {
        var lastPoi: PointOfInterest? = null
        for (poi in getPointsOfInterest()) {
            if (poi is DestinationPoi) {
                lastPoi = poi
                continue
            }
            if (!poi.shouldDraw(locations, scale)) continue
            poi.draw(xOffset, yOffset, scale)
            locations.add(poi.x to poi.z)
        }
        // draw destination last since it always has to be on top
        lastPoi?.draw(xOffset, yOffset, scale)
    }

    fun drawTooltips(
        x: Float,
        y: Float,
        mouseX: Int,
        mouseY: Int,
        scale: Float,
        locations: MutableList<Pair<Float, Float>>
    ): Boolean {
        val xScaled = mouseX / scale - x
        val yScaled = mouseY / scale - y
        for (poi in getPointsOfInterest()) {
            if ((!locations.contains(poi.x to poi.z) && poi !is DestinationPoi) || !poi.shouldDrawTooltip(
                    xScaled,
                    yScaled,
                    xOffset,
                    yOffset,
                    scale
                )
            ) continue
            poi.drawTooltip(mouseX, mouseY)
            if (GuiUtils.leftClicked) poi.onLeftClick()
            if (GuiUtils.rightClicked) poi.onRightClick()
            return true
        }
        return false
    }

    fun getImage(y: Int): Textures {
        for ((height, image) in images) {
            if (height >= y) return image
        }
        return images[0]!!
    }

    private fun getPointsOfInterest(): List<PointOfInterest> {
        val list = mutableListOf<PointOfInterest>()
        list.addAll(portals)
        val dest = NavigationHandler.destinationPio
        if (dest.destination != null && dest.destination!!.island == this) list.add(
            0, NavigationHandler.destinationPio
        )
        return list
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

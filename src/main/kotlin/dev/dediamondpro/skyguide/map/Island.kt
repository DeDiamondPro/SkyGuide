package dev.dediamondpro.skyguide.map

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
    }

    fun draw(y: Int) {
        getImage(y).draw(topX + xOffset, topY + yOffset, width, height)
    }

    fun drawLast(scale: Float, locations: MutableList<Pair<Float, Float>>) {
        for (poi in getPointsOfInterest()) {
            if (!poi.shouldDraw(locations, scale)) continue
            poi.draw(xOffset, yOffset, scale)
            locations.add(poi.x to poi.z)
        }
    }

    fun drawUnscaled(
        x: Float,
        y: Float,
        mouseX: Int,
        mouseY: Int,
        scale: Float,
        locations: MutableList<Pair<Float, Float>>
    ) {
        val xScaled = mouseX / scale - x
        val yScaled = mouseY / scale - y
        for (poi in getPointsOfInterest()) {
            if (!locations.contains(poi.x to poi.z) || !poi.shouldDrawTooltip(
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
            break
        }
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
        return list
    }

    fun routeTo(island: String) {
        println(discoverPortals(island, this, mutableListOf()))
    }

    private fun discoverPortals(
        destination: String,
        currentIsland: Island,
        visitedIslands: MutableList<String>
    ): MutableList<Portal>? {
        visitedIslands.add(currentIsland.zone!!)
        for (portal in currentIsland.portals) {
            if (portal.destination == null || visitedIslands.contains(portal.destination)) continue
            if (portal.destination == destination) return mutableListOf(portal)
            val currentWorld = SkyblockMap.getCurrentWorld() ?: continue
            val destinationIsland = currentWorld[portal.destination] ?: continue
            val path = discoverPortals(destination, destinationIsland, visitedIslands) ?: continue
            path.add(0, portal)
            return path
        }
        return null
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

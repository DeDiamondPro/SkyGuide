package dev.dediamondpro.skyguide.map

import cc.polyfrost.oneconfig.libs.universal.UGraphics
import dev.dediamondpro.skyguide.compat.SkytilsCompat
import dev.dediamondpro.skyguide.map.navigation.NavigationHandler
import dev.dediamondpro.skyguide.map.poi.*
import dev.dediamondpro.skyguide.utils.GuiUtils
import kotlin.math.cos
import kotlin.math.sin

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
    var images: MutableList<Textures>,
    val portals: MutableList<Portal> = mutableListOf(),
    val npcs: MutableList<Npc> = mutableListOf(),
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
        for (poi in getPointsOfInterest()) poi.island = this
    }

    fun draw(x: Float?, y: Float?, z: Float?) {
        (if (x == null || y == null || z == null) getDefaultImage() else getImage(x, y, z)).draw(
            topX + xOffset,
            topY + yOffset,
            width,
            height
        )
    }

    fun drawLast(x: Float, y: Float, scale: Float, locations: MutableList<Pair<Float, Float>>) {
        var lastPoi: PointOfInterest? = null
        for (poi in getPointsOfInterest()) {
            if (poi is DestinationPoi) {
                lastPoi = poi
                continue
            }
            if (!poi.shouldDraw(locations, scale)) continue
            poi.draw(x, y, xOffset, yOffset, scale)
            locations.add(poi.x to poi.z)
        }
        // draw destination last since it always has to be on top
        UGraphics.disableDepth()
        lastPoi?.draw(x, y, xOffset, yOffset, scale)
        UGraphics.enableDepth()
    }

    fun drawPioMiniMap(x: Float, y: Float, originX: Double, originY: Double, scale: Float, rotation: Double) {
        val locations = mutableListOf<Pair<Float, Float>>()
        var lastPoi: PointOfInterest? = null
        for (poi in getPointsOfInterest()) {
            if (poi is DestinationPoi) {
                lastPoi = poi
                continue
            }
            if (!poi.shouldDraw(locations, scale)) continue
            poi.drawRaw(
                (cos(rotation) * (x + poi.x * scale - originX) + sin(rotation) * (y + poi.z * scale - originY) + originX).toFloat(),
                (-sin(rotation) * (x + poi.x * scale - originX) + cos(rotation) * (y + poi.z * scale - originY) + originY).toFloat(),
            )
            locations.add(poi.x to poi.z)
        }
        // draw destination last since it always has to be on top
        UGraphics.disableDepth()
        lastPoi?.drawRaw(
            (cos(rotation) * (x + lastPoi.x * scale - originX) + sin(rotation) * (y + lastPoi.z * scale - originY) + originX).toFloat(),
            (-sin(rotation) * (x + lastPoi.x * scale - originX) + cos(rotation) * (y + lastPoi.z * scale - originY) + originY).toFloat(),
        )
        UGraphics.enableDepth()
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

    fun getImage(x: Float, y: Float, z: Float): Textures {
        for (image in images) if (image.parsedCondition.evaluate(x, y, z)) return image
        return getDefaultImage()
    }

    fun getDefaultImage(): Textures {
        for (image in images) if (image.parsedCondition.isEmpty()) return image
        return images.first()
    }

    private fun getPointsOfInterest(): List<PointOfInterest> {
        val list = mutableListOf<PointOfInterest>()
        list.addAll(portals)
        list.addAll(npcs)
        if (SkytilsCompat.waypoints.containsKey(this)) list.addAll(SkytilsCompat.waypoints[this]!!)
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

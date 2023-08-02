package dev.dediamondpro.skyguide.map

import dev.dediamondpro.skyguide.compat.INEUCompat
import dev.dediamondpro.skyguide.compat.SkytilsCompat
import dev.dediamondpro.skyguide.config.Config
import dev.dediamondpro.skyguide.map.navigation.NavigationHandler
import dev.dediamondpro.skyguide.map.poi.*
import dev.dediamondpro.skyguide.utils.GuiUtils
import dev.dediamondpro.skyguide.utils.ItemUtils
import gg.essential.universal.UGraphics
import kotlinx.serialization.Transient
import net.minecraft.item.ItemStack
import kotlin.math.*

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
) : Searchable {
    @Transient
    override val searchString = name.replaceFirstChar { it.titlecase() }

    @Transient
    override val searchDescription = "A Skyblock Island"

    @Transient
    override var island: Island? = this

    @Transient
    override val x: Float = (topX + bottomX) / 2

    @Transient
    override val z: Float = (topY + bottomY) / 2

    @Transient
    override val scale: Float = 1f

    @Transient
    override val skull: ItemStack = islandSkull

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
            if (!poi.shouldDraw(locations, scale, Config.POIScale)) continue
            poi.draw(x, y, xOffset, yOffset, scale, Config.POIScale)
            locations.add(poi.x to poi.z)
        }
        // draw destination last since it always has to be on top
        UGraphics.disableDepth()
        lastPoi?.draw(x, y, xOffset, yOffset, scale, Config.POIScale)
        UGraphics.enableDepth()
    }

    fun drawPioMiniMap(x: Float, y: Float, originX: Double, originY: Double, scale: Float, rotation: Double) {
        val locations = mutableListOf<Pair<Float, Float>>()
        var lastPoi: PointOfInterest? = null
        for (poi in getPointsOfInterest(true)) {
            if (poi is DestinationPoi) {
                lastPoi = poi
                continue
            }
            if (!poi.shouldDraw(locations, scale, Config.POIScaleMiniMap)) continue
            poi.drawRaw(
                (cos(rotation) * (x + poi.x * scale - originX) + sin(rotation) * (y + poi.z * scale - originY) + originX).toFloat(),
                (-sin(rotation) * (x + poi.x * scale - originX) + cos(rotation) * (y + poi.z * scale - originY) + originY).toFloat(),
                Config.POIScaleMiniMap
            )
            locations.add(poi.x to poi.z)
        }
        // draw destination last since it always has to be on top
        UGraphics.disableDepth()
        lastPoi?.drawRaw(
            (cos(rotation) * (x + lastPoi.x * scale - originX) + sin(rotation) * (y + lastPoi.z * scale - originY) + originX).toFloat(),
            (-sin(rotation) * (x + lastPoi.x * scale - originX) + cos(rotation) * (y + lastPoi.z * scale - originY) + originY).toFloat(),
            Config.POIScaleMiniMap
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
                    xScaled, yScaled,
                    xOffset, yOffset,
                    scale, Config.POIScale
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

    private fun getPointsOfInterest(miniMap: Boolean = false): List<PointOfInterest> {
        val list = mutableListOf<PointOfInterest>()
        if (Config.showWarps && !miniMap || Config.showWarpsMiniMap && miniMap) list.addAll(portals)
        INEUCompat.instance?.getCurrentlyTrackedWaypoint()?.let { if (it.island == this) list.add(it) }
        if (SkytilsCompat.waypoints.containsKey(this)) list.addAll(SkytilsCompat.waypoints[this]!!)
        if (Config.showNpcs && !miniMap || Config.showNpcsMiniMap && miniMap) list.addAll(npcs)
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

    fun findClosestPortal(x: Float, y: Float?, z: Float): Portal? {
        var lowestDist = Float.MAX_VALUE
        var closestPortal: Portal? = null
        for (portal in portals) {
            if (portal.command == null || (portal.mvp && !Config.showMVPWarps)) continue
            val distance = sqrt(
                (x - portal.x).pow(2f) + (if (y == null) 0f else (y - portal.y).pow(
                    2f
                )) + (z - portal.z).pow(2f)
            )
            if (distance < lowestDist) {
                lowestDist = distance
                closestPortal = portal
            }
        }
        return closestPortal
    }

    companion object {
        private val islandSkull = ItemUtils.createSkull(
            "cc9258c4-76d8-2dee-a648-510538c15581",
            "eyJ0aW1lc3RhbXAiOjE1NTkyMTU0MTY5MDksInByb2ZpbGVJZCI6IjQxZDNhYmMyZDc0OTQwMGM5MDkwZDU0MzRkMDM4MzFiIiwicHJvZmlsZU5hbWUiOiJNZWdha2xvb24iLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2Q3Y2M2Njg3NDIzZDA1NzBkNTU2YWM1M2UwNjc2Y2I1NjNiYmRkOTcxN2NkODI2OWJkZWJlZDZmNmQ0ZTdiZjgifX19"
        )

        fun getXOffset(): Float {
            return SkyblockMap.getCurrentIsland()?.xOffset ?: 0f
        }

        fun getYOffset(): Float {
            return SkyblockMap.getCurrentIsland()?.yOffset ?: 0f
        }
    }
}

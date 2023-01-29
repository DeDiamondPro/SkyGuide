package dev.dediamondpro.skyguide.map.poi

import dev.dediamondpro.skyguide.config.Config
import dev.dediamondpro.skyguide.map.Island
import dev.dediamondpro.skyguide.map.navigation.Destination
import dev.dediamondpro.skyguide.map.navigation.NavigationHandler
import dev.dediamondpro.skyguide.utils.RenderUtils
import gg.essential.universal.UGraphics
import java.awt.Color

class SkytilsWaypoint(
    private val name: String,
    private val enabled: Boolean,
    override var island: Island?,
    private val color: Color,
    override val x: Float,
    override val y: Float,
    override val z: Float,
) : PointOfInterest() {

    override fun shouldDraw(): Boolean {
        return Config.skytilsWaypoints && (enabled || Config.disabledSkytilsWaypoints)
    }

    override fun drawIcon(x: Float, y: Float) {
        UGraphics.color4f(
            color.red / 255f,
            color.green / 255f,
            color.blue / 255f,
            color.alpha / 255f
        )
        RenderUtils.drawImage(
            "/assets/skyguide/pin.png",
            x - 8f,
            y - 8f,
            16f,
            16f
        )
        UGraphics.color4f(1f, 1f, 1f, 1f)
    }

    override fun getTooltip(): List<String> {
        return listOf(
            name,
            "Waypoint imported from Skytils",
            "Right click set destination"
        )
    }

    override fun onLeftClick() {
    }

    override fun onRightClick() {
        if (island == null) return
        NavigationHandler.navigateTo(Destination(island!!, x, y, z, name))
    }

    override fun drawBackground(x: Float, y: Float) {}
}
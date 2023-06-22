package dev.dediamondpro.skyguide.map.poi

import dev.dediamondpro.skyguide.compat.INEUCompat
import dev.dediamondpro.skyguide.config.Config
import dev.dediamondpro.skyguide.map.Island
import dev.dediamondpro.skyguide.utils.GuiUtils
import dev.dediamondpro.skyguide.utils.RenderUtils
import gg.essential.universal.UGraphics

data class NEUWaypoint(
    val name: String,
    override val x: Float,
    override val y: Float,
    override val z: Float,
    override var island: Island?,
    val neuCompat: INEUCompat,
) : PointOfInterest() {
    override fun shouldDraw(): Boolean {
        return Config.neuWaypoints
    }

    override fun drawIcon(x: Float, y: Float) {
        UGraphics.color4f(1f, 0f, 0f, 1f)
        RenderUtils.drawImage(
            "/assets/skyguide/pin.png",
            x - 8f,
            y - 8f,
            16f,
            16f
        )
    }

    override fun getTooltip(): List<String> {
        return mutableListOf(
            name,
            "Left click to teleport to nearest warp",
            "Right click to remove waypoint",
            "Waypoint imported from NEU"
        )
    }

    override fun onLeftClick() {
        neuCompat.useWarp()
        GuiUtils.displayScreen(null)
    }

    override fun onRightClick() {
        neuCompat.untrackWaypoint()
    }

    override fun drawBackground(x: Float, y: Float) {}
}
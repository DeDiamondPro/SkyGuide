package dev.dediamondpro.skyguide.map.poi

import dev.dediamondpro.skyguide.map.navigation.Destination
import dev.dediamondpro.skyguide.utils.RenderUtils

class DestinationPoi(var destination: Destination?) : PointOfInterest() {
    override val x: Float
        get() = destination?.x ?: 0f
    override val y: Float
        get() = destination?.y ?: 0f
    override val z: Float
        get() = destination?.z ?: 0f

    override fun shouldDraw(): Boolean {
        return destination != null
    }

    override fun drawIcon(x: Float, y: Float, scale: Float) {
        RenderUtils.drawImage(
            "/assets/skyguide/pin.png",
            x - 8f / scale,
            y - 8f / scale,
            16f / scale,
            16f / scale
        )
    }

    override fun getTooltip(): List<String> {
        if (destination == null) return mutableListOf()
        return mutableListOf(
            destination!!.name,
            "Left click to teleport to nearest warp",
            "Right click to remove waypoint"
        )
    }

    override fun onLeftClick() {
        TODO("Not yet implemented")
    }

    override fun onRightClick() {
        destination = null
    }

    override fun drawBackground(x: Float, y: Float, scale: Float) {}
}
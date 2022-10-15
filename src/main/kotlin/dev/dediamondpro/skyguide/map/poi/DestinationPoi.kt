package dev.dediamondpro.skyguide.map.poi

import dev.dediamondpro.skyguide.config.Config
import dev.dediamondpro.skyguide.map.navigation.Destination
import dev.dediamondpro.skyguide.map.navigation.NavigationHandler
import dev.dediamondpro.skyguide.utils.GuiUtils
import dev.dediamondpro.skyguide.utils.RenderUtils
import gg.essential.universal.UChat
import gg.essential.universal.wrappers.UPlayer
import kotlin.math.pow
import kotlin.math.sqrt

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

    override fun drawIcon(x: Float, y: Float) {
        RenderUtils.drawImage(
            "/assets/skyguide/pin.png",
            x - 8f,
            y - 8f,
            16f,
            16f
        )
    }

    override fun getTooltip(): List<String> {
        if (destination == null) return mutableListOf()
        return mutableListOf(
            destination!!.name.replaceFirstChar { it.uppercase() },
            "Left click to teleport to nearest warp",
            "Right click to remove waypoint"
        )
    }

    override fun onLeftClick() {
        if (destination == null) return
        var lowestDist = Float.MAX_VALUE
        var closestPortal: Portal? = null
        for (portal in destination!!.island.portals) {
            if (portal.command == null || (portal.mvp && !Config.showMVPWarps)) continue
            val distance = sqrt(
                (x - portal.x).pow(2f) + (if (destination!!.y == null) 0f else (y - portal.y).pow(
                    2f
                )) + (z - portal.z).pow(2f)
            )
            if (distance < lowestDist) {
                lowestDist = distance
                closestPortal = portal
            }
        }
        if (closestPortal != null) UChat.say("/${closestPortal.command}")
        else UChat.chat("Could not find a warp!")
        GuiUtils.displayScreen(null)
    }

    override fun onRightClick() {
        NavigationHandler.clearNavigation()
    }

    override fun drawBackground(x: Float, y: Float) {}
}
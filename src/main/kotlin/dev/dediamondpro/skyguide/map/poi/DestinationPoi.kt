package dev.dediamondpro.skyguide.map.poi

import cc.polyfrost.oneconfig.libs.universal.UChat
import cc.polyfrost.oneconfig.libs.universal.UGraphics
import dev.dediamondpro.skyguide.config.Config
import dev.dediamondpro.skyguide.map.navigation.Destination
import dev.dediamondpro.skyguide.map.navigation.NavigationHandler
import dev.dediamondpro.skyguide.utils.GuiUtils
import dev.dediamondpro.skyguide.utils.RenderUtils
import net.minecraft.util.EnumChatFormatting

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
        UGraphics.color4f(
            Config.pinColor.red / 255f,
            Config.pinColor.green / 255f,
            Config.pinColor.blue / 255f,
            Config.pinColor.alpha / 255f
        )
        RenderUtils.drawImage(
            "/assets/skyguide/pin.png",
            x - 8f * scale,
            y - 8f * scale,
            16f * scale,
            16f * scale
        )
        UGraphics.color4f(1f, 1f, 1f, 1f)
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
        val closestPortal = destination!!.island.findClosestPortal(x, destination?.y, z)
        if (closestPortal != null) UChat.say("/${closestPortal.command}")
        else UChat.chat("${EnumChatFormatting.RED}Could not find a warp!")
        GuiUtils.displayScreen(null)
    }

    override fun onRightClick() {
        NavigationHandler.clearNavigation()
    }

    override fun drawBackground(x: Float, y: Float, scale: Float) {}
}
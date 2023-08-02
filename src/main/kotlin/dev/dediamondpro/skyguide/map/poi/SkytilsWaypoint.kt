package dev.dediamondpro.skyguide.map.poi

import cc.polyfrost.oneconfig.libs.universal.UChat
import cc.polyfrost.oneconfig.libs.universal.UGraphics
import dev.dediamondpro.skyguide.config.Config
import dev.dediamondpro.skyguide.map.Island
import dev.dediamondpro.skyguide.map.navigation.Destination
import dev.dediamondpro.skyguide.map.navigation.NavigationHandler
import dev.dediamondpro.skyguide.utils.GuiUtils
import dev.dediamondpro.skyguide.utils.RenderUtils
import net.minecraft.util.EnumChatFormatting
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

    override fun drawIcon(x: Float, y: Float, scale: Float) {
        UGraphics.color4f(
            color.red / 255f,
            color.green / 255f,
            color.blue / 255f,
            color.alpha / 255f
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
        return listOf(
            name,
            "Left click to teleport to nearest warp",
            "Right click to set destination",
            "Waypoint imported from Skytils"
        )
    }

    override fun onLeftClick() {
        if (island == null) return
        val closestPortal = island?.findClosestPortal(x, y, z)
        if (closestPortal != null) UChat.say("/${closestPortal.command}")
        else UChat.chat("${EnumChatFormatting.RED}Could not find a warp!")
        GuiUtils.displayScreen(null)
    }

    override fun onRightClick() {
        if (island == null) return
        NavigationHandler.navigateTo(Destination(island!!, x, y, z, name))
    }

    override fun drawBackground(x: Float, y: Float, scale: Float) {}
}
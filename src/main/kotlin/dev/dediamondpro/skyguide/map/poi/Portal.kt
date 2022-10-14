package dev.dediamondpro.skyguide.map.poi

import dev.dediamondpro.skyguide.config.Config
import dev.dediamondpro.skyguide.map.navigation.Destination
import dev.dediamondpro.skyguide.map.navigation.NavigationHandler
import dev.dediamondpro.skyguide.utils.GuiUtils
import dev.dediamondpro.skyguide.utils.RenderUtils
import gg.essential.universal.UMinecraft
import kotlinx.serialization.Serializable

/**
 * @param name The name of the portal
 * @param destination The destination of the portal, null if the portal is command only
 * @param command The command to teleport to the portal, null if no command available
 * @param x The X coordinate of the portal
 * @param y The Y coordinate of the portal
 * @param z The Z coordinate of the portal
 */
@Serializable
data class Portal(
    val name: String = "",
    val destination: String? = null,
    val command: String? = null,
    val mvp: Boolean = false,
    override val x: Float,
    override val y: Float,
    override val z: Float,
) : PointOfInterest() {
    override fun shouldDraw(): Boolean {
        return command != null && (!mvp || Config.showMVPWarps)
    }

    override fun drawIcon(x: Float, y: Float, scale: Float) {
        RenderUtils.drawImage(
            "/assets/skyguide/portal.png",
            x - 6f / scale,
            y - 9f / scale,
            12f / scale,
            18f / scale
        )
    }

    override fun getTooltip(): List<String> {
        return mutableListOf("Warp to $name", "Left Click to teleport", "Right Click to navigate")
    }

    override fun onLeftClick() {
        UMinecraft.getMinecraft().thePlayer.sendChatMessage("/$command")
        GuiUtils.displayScreen(null)
    }

    override fun onRightClick() {
        NavigationHandler.navigateTo(Destination(island!!, x, y, z, name))
        GuiUtils.displayScreen(null)
    }
}

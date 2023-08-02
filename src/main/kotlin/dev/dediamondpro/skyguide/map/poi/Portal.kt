package dev.dediamondpro.skyguide.map.poi

import cc.polyfrost.oneconfig.libs.universal.UMinecraft
import dev.dediamondpro.skyguide.config.Config
import dev.dediamondpro.skyguide.map.navigation.*
import dev.dediamondpro.skyguide.utils.ItemUtils
import dev.dediamondpro.skyguide.utils.RenderUtils
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import net.minecraft.item.ItemStack

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
) : PointOfInterest(), NavigationProvider, Searchable {
    override val destinations = if (destination == null) listOf() else listOf(destination)

    @Transient
    override val searchString: String = "Portal to $name"

    @Transient
    override val searchDescription by lazy { "A portal in ${island?.name}" }

    @Transient
    override val scale: Float = 5f

    @Transient
    override val skull: ItemStack = portalSkull

    override fun shouldDraw(): Boolean {
        return command != null && (!mvp || Config.showMVPWarps)
    }

    override fun drawIcon(x: Float, y: Float, scale: Float) {
        RenderUtils.drawImage(
            "/assets/skyguide/portal.png",
            x - 6f * scale,
            y - 9f * scale,
            12f * scale,
            18f * scale
        )
    }

    override fun getTooltip(): List<String> {
        return mutableListOf("Warp to $name", "Left Click to teleport", "Right Click to navigate")
    }

    override fun onLeftClick() {
        UMinecraft.getMinecraft().thePlayer.sendChatMessage("/$command")
    }

    override fun onRightClick() {
        NavigationHandler.navigateTo(Destination(island!!, x, y, z, name))
    }

    override fun getAction(destination: Destination): NavigationAction = PortalAction(this, destination)

    companion object {
        private val portalSkull = ItemUtils.createSkull(
            "ae1e7567-0b38-4677-97ae-e3fd99d39fbf",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjBiZmMyNTc3ZjZlMjZjNmM2ZjczNjVjMmM0MDc2YmNjZWU2NTMxMjQ5ODkzODJjZTkzYmNhNGZjOWUzOWIifX19"
        )
    }
}

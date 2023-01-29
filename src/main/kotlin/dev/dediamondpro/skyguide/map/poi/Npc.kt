package dev.dediamondpro.skyguide.map.poi

import cc.polyfrost.oneconfig.libs.universal.UDesktop
import cc.polyfrost.oneconfig.libs.universal.UGraphics
import dev.dediamondpro.skyguide.config.Config
import dev.dediamondpro.skyguide.map.navigation.*
import dev.dediamondpro.skyguide.utils.ItemUtils
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.net.URI

@Serializable
class Npc(
    val name: String,
    val wiki: String? = null,
    val owner: String,
    val texture: String,
    override val x: Float,
    override val y: Float,
    override val z: Float,
    override val destinations: List<String> = listOf()
) : PointOfInterest(), NavigationProvider {
    @Transient
    private val skull = ItemUtils.createSkull(owner, texture)

    override fun shouldDraw(): Boolean {
        return Config.showNpcs
    }

    override fun drawIcon(x: Float, y: Float) {
        UGraphics.GL.pushMatrix()
        UGraphics.GL.translate(x - 16, y - 16, 0f)
        UGraphics.GL.scale(2.0, 2.0, 1.0)
        ItemUtils.drawItemStack(skull, 0, 0)
        UGraphics.GL.popMatrix()
    }

    override fun getTooltip(): List<String> {
        val list = mutableListOf(name.replaceFirstChar { it.uppercaseChar() }, "Right click to set destination")
        if (wiki != null) list.add(1, "Left click to open wiki page")
        return list
    }

    override fun onLeftClick() {
        if (wiki == null) return
        UDesktop.browse(URI(wiki))
    }

    override fun onRightClick() {
        NavigationHandler.navigateTo(Destination(island!!, x, y, z, name))
    }

    override fun getAction(destination: Destination): NavigationAction = NpcAction(this, destination)
}
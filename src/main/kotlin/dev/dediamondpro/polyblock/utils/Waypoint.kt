package dev.dediamondpro.polyblock.utils

import cc.polyfrost.oneconfig.config.core.OneColor
import dev.dediamondpro.polyblock.map.SkyblockMap
import net.minecraft.util.BlockPos

data class Waypoint(
    val zone: String,
    val x: Float,
    val y: Float,
    var color: Int = OneColor.HSBAtoARGB((Math.random() * 360f).toFloat(), 100f, 100f, 255)
) {

    fun draw(partialTicks: Float) {
        if (!shouldDraw()) return
        RenderUtils.renderBeaconBeam(BlockPos(getOffsetX().toInt(), 0, getOffsetY().toInt()), color, partialTicks)
    }

    private fun getOffsetX(): Float {
        val offset = SkyblockMap.getCurrentIsland()?.xOffset ?: 0f
        return x - offset
    }

    private fun getOffsetY(): Float {
        val offset = SkyblockMap.getCurrentIsland()?.yOffset ?: 0f
        return y - offset
    }

    private fun shouldDraw(): Boolean {
        return SkyblockMap.isZoneInWorld(zone)
    }
}
package dev.dediamondpro.skyguide.map.navigation

import dev.dediamondpro.skyguide.map.SkyblockMap
import dev.dediamondpro.skyguide.map.poi.Portal
import dev.dediamondpro.skyguide.utils.RenderUtils
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumChatFormatting
import java.awt.Color
import javax.vecmath.Vector3f

class PortalAction(private val portal: Portal, private val destination: Destination) : NavigationAction {
    override fun drawAction(partialTicks: Float) {
        RenderUtils.renderBeaconBeam(
            BlockPos(portal.x.toDouble(), portal.y.toDouble(), portal.z.toDouble()),
            Color.RED.rgb,
            partialTicks
        )
        RenderUtils.renderWayPoint(
            mutableListOf(
                "Warp to ${EnumChatFormatting.BLUE}${SkyblockMap.getIslandByZone(portal.destination ?: "")?.name ?: "unknown"}",
                "to reach ${EnumChatFormatting.BLUE}${destination.name}"
            ),
            Vector3f(portal.x, portal.y + 2, portal.z),
            partialTicks
        )
    }
}
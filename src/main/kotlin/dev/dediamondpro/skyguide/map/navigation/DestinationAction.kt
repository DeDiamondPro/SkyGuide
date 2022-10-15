package dev.dediamondpro.skyguide.map.navigation

import dev.dediamondpro.skyguide.utils.RenderUtils
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumChatFormatting
import java.awt.Color
import javax.vecmath.Vector3f

class DestinationAction(private val destination: Destination) : NavigationAction {
    override fun drawAction(partialTicks: Float) {
        RenderUtils.renderBeaconBeam(
            BlockPos(
                destination.x.toDouble(),
                destination.y?.toDouble() ?: 0.0,
                destination.z.toDouble()
            ), Color.RED.rgb, partialTicks
        )
        if (destination.y != null) RenderUtils.renderWayPoint(
            mutableListOf("${EnumChatFormatting.BLUE}${destination.name.replaceFirstChar { it.uppercase() }}"),
            Vector3f(destination.x, destination.y + 2f, destination.z),
            partialTicks
        )
    }
}
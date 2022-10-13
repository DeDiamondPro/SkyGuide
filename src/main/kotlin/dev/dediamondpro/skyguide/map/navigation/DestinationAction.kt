package dev.dediamondpro.skyguide.map.navigation

import dev.dediamondpro.skyguide.utils.RenderUtils
import net.minecraft.util.BlockPos
import java.awt.Color
import javax.vecmath.Vector3f

class DestinationAction(val destination: Destination) : NavigationAction {
    override fun drawAction(partialTicks: Float) {
        if(destination.y != null) {
            RenderUtils.renderBeaconBeam(
                BlockPos(
                    destination.x.toDouble(),
                    destination.y.toDouble(),
                    destination.z.toDouble()
                ), Color.RED.rgb, partialTicks
            )
        }
        RenderUtils.renderWayPoint(
            mutableListOf("Destination"),
            Vector3f(destination.x, (destination.y ?: 0f) + 2f, destination.z),
            partialTicks
        )
    }
}
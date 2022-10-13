package dev.dediamondpro.skyguide.map.navigation

import dev.dediamondpro.skyguide.map.poi.Portal
import dev.dediamondpro.skyguide.utils.RenderUtils
import javax.vecmath.Vector3f

class PortalAction(private val portal: Portal) : NavigationAction {
    override fun drawAction(partialTicks: Float) {
        RenderUtils.renderWayPoint(
            mutableListOf(portal.name),
            Vector3f(portal.x, portal.y + 2, portal.z),
            partialTicks
        )
    }
}
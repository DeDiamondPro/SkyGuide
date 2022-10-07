package dev.dediamondpro.skyguide.handlers

import dev.dediamondpro.skyguide.config.Config
import dev.dediamondpro.skyguide.utils.SBInfo
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

class WaypointHandler {
    @SubscribeEvent
    fun onRender(event: RenderWorldLastEvent) {
        if (!SBInfo.inSkyblock) return
        for (waypoint in Config.waypoints) {
            waypoint.draw(event.partialTicks)
        }
    }
}
package cc.polyfrost.polyblock.handlers

import cc.polyfrost.polyblock.config.BlockConfig
import cc.polyfrost.polyblock.utils.SBInfo
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

class WaypointHandler {
    @SubscribeEvent
    fun onRender(event: RenderWorldLastEvent) {
        if (!BlockConfig.enabled || !SBInfo.inSkyblock) return
        for (waypoint in BlockConfig.waypoints) {
            waypoint.draw(event.partialTicks)
        }
    }
}
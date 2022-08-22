package cc.polyfrost.polyblock.handlers

import cc.polyfrost.polyblock.config.BlockConfig
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

class WaypointHandler {
    @SubscribeEvent
    fun onRender(event: RenderWorldLastEvent) {
        for (waypoint in BlockConfig.waypoints) {
            waypoint.draw(event.partialTicks)
        }
    }
}
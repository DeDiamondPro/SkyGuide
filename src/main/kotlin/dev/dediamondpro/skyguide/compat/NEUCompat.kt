package dev.dediamondpro.skyguide.compat

import dev.dediamondpro.skyguide.map.SkyblockMap
import dev.dediamondpro.skyguide.map.poi.NEUWaypoint
import io.github.moulberry.notenoughupdates.NotEnoughUpdates
import net.minecraftforge.client.event.GuiOpenEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent

@Deprecated("use INEUCompat.instance instead")
class NEUCompat : INEUCompat {
    val neu = NotEnoughUpdates.INSTANCE

    init {
        neu.navigation
    }

    private var waypoint: NEUWaypoint? = null
    private var tick = 0

    @SubscribeEvent
    fun onGuiOpen(event: GuiOpenEvent) {
        recalculateWaypoint()
    }


    @SubscribeEvent
    fun onTick(event: TickEvent.WorldTickEvent) {
        if ((tick++) % 40 == 0)
            recalculateWaypoint()
    }

    private fun recalculateWaypoint() {
        val nav = neu.navigation
        waypoint = if (nav.trackedWaypoint == null) null else NEUWaypoint(
            nav.displayName, nav.position.x.toFloat(), nav.position.y.toFloat(), nav.position.z.toFloat(),
            SkyblockMap.getIslandByZone(nav.island),
            this
        )
    }

    override fun useWarp() {
        neu.navigation.useWarpCommand()
    }

    override fun getCurrentlyTrackedWaypoint(): NEUWaypoint? {
        return waypoint
    }

    override fun untrackWaypoint() {
        neu.navigation.untrackWaypoint()
        recalculateWaypoint()
    }
}
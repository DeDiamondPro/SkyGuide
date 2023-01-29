package dev.dediamondpro.skyguide.compat

import dev.dediamondpro.skyguide.map.Island
import dev.dediamondpro.skyguide.map.SkyblockMap
import dev.dediamondpro.skyguide.map.poi.SkytilsWaypoint
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent
import java.awt.Color

class SkytilsCompat {
    private var ticks = 0

    @SubscribeEvent
    fun onTick(e: ClientTickEvent) {
        if (e.phase == TickEvent.Phase.START) return
        ticks++
        if (ticks >= 20) {
            waypoints = getSkytilsWaypoints()
            ticks = 0
        }
    }

    companion object {
        var waypoints: Map<Island, Set<SkytilsWaypoint>> = mapOf()
            private set
        private var skytilsLoaded: Boolean = true
        private val waypointsClass: Class<*>? by lazy { Class.forName("gg.skytils.skytilsmod.features.impl.handlers.Waypoints") }
        private val waypointCategoryClass: Class<*>? by lazy { Class.forName("gg.skytils.skytilsmod.features.impl.handlers.WaypointCategory") }
        private val waypointClass: Class<*>? by lazy { Class.forName("gg.skytils.skytilsmod.features.impl.handlers.Waypoint") }
        private val islandClass: Class<*>? by lazy { Class.forName("gg.skytils.skytilsmod.utils.SkyblockIsland") }
        private val waypointCategoryField by lazy {
            val field = waypointsClass?.getDeclaredField("categories")
            field?.isAccessible = true
            field
        }
        private val waypointsField by lazy {
            val field = waypointCategoryClass?.getDeclaredField("waypoints")
            field?.isAccessible = true
            field
        }
        private val islandField by lazy {
            val field = waypointCategoryClass?.getDeclaredField("island")
            field?.isAccessible = true
            field
        }
        private val modeField by lazy {
            val field = islandClass?.getDeclaredField("mode")
            field?.isAccessible = true
            field
        }
        private val nameField by lazy {
            val field = waypointClass?.getDeclaredField("name")
            field?.isAccessible = true
            field
        }
        private val enabledField by lazy {
            val field = waypointClass?.getDeclaredField("enabled")
            field?.isAccessible = true
            field
        }
        private val colorField by lazy {
            val field = waypointClass?.getDeclaredField("color")
            field?.isAccessible = true
            field
        }
        private val xField by lazy {
            val field = waypointClass?.getDeclaredField("x")
            field?.isAccessible = true
            field
        }
        private val yField by lazy {
            val field = waypointClass?.getDeclaredField("y")
            field?.isAccessible = true
            field
        }
        private val zField by lazy {
            val field = waypointClass?.getDeclaredField("z")
            field?.isAccessible = true
            field
        }

        private fun getSkytilsWaypoints(): Map<Island, Set<SkytilsWaypoint>> {
            if (!skytilsLoaded) return mapOf()
            val result = mutableMapOf<Island, MutableSet<SkytilsWaypoint>>()
            try {
                val waypointCategories = waypointCategoryField!![null] as HashSet<*>
                for (category in waypointCategories) {
                    val island =
                        SkyblockMap.getIslandByZone(modeField!![islandField!![category]!!]!! as String) ?: continue
                    (waypointsField!![category] as Set<*>).forEach {
                        val waypoint = SkytilsWaypoint(
                            nameField!![it] as String,
                            enabledField!![it] as Boolean,
                            island,
                            colorField!![it] as Color,
                            (xField!![it] as Int).toFloat(),
                            (yField!![it] as Int).toFloat(),
                            (zField!![it] as Int).toFloat()
                        )
                        if (!result.containsKey(island)) result[island] = mutableSetOf()
                        result[island]!!.add(waypoint)
                    }
                }
            } catch (e: Exception) {
                println("An error has occurred in the Skytils compatibility layer, disabling...")
                skytilsLoaded = false
            }
            return result
        }
    }
}
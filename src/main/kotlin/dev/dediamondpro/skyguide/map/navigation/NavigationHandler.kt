package dev.dediamondpro.skyguide.map.navigation

import dev.dediamondpro.skyguide.map.Island
import dev.dediamondpro.skyguide.map.SkyblockMap
import dev.dediamondpro.skyguide.map.poi.DestinationPoi
import dev.dediamondpro.skyguide.map.poi.Portal
import gg.essential.universal.UChat
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

class NavigationHandler {

    @SubscribeEvent
    fun onRender(event: RenderWorldLastEvent) {
        val action = actions[SkyblockMap.getCurrentIsland() ?: return]
        if (action == null && destinationPio.destination != null) navigateTo(destinationPio.destination!!)
        if (action == null) return
        action.drawAction(event.partialTicks)
    }

    companion object {
        private val actions = mutableMapOf<Island, NavigationAction>()
        val destinationPio = DestinationPoi(null)

        fun navigateTo(destination: Destination) {
            clearNavigation()
            val currentIsland = SkyblockMap.getCurrentIsland() ?: return
            if (currentIsland != destination.island) {
                val route = findRouteToIsland(
                    destination.island.zone ?: return,
                    currentIsland,
                    mutableListOf()
                )
                if (route == null) {
                    UChat.chat("Could not find a route!")
                    return
                }
                for (step in route) actions[step.key] = PortalAction(step.value, destination)
            }
            actions[destination.island] = DestinationAction(destination)
            destinationPio.destination = destination
        }

        fun clearNavigation() {
            actions.clear()
            destinationPio.destination = null
        }

        private fun findRouteToIsland(
            destination: String,
            currentIsland: Island,
            visitedIslands: MutableList<String>
        ): MutableMap<Island, Portal>? {
            visitedIslands.add(currentIsland.zone!!)
            for (portal in currentIsland.portals) {
                if (portal.destination == null || visitedIslands.contains(portal.destination)) continue
                if (portal.destination == destination) return mutableMapOf(currentIsland to portal)
                val currentWorld = SkyblockMap.getCurrentWorld() ?: continue
                val destinationIsland = currentWorld[portal.destination] ?: continue
                val path = findRouteToIsland(destination, destinationIsland, visitedIslands) ?: continue
                path[currentIsland] = portal
                return path
            }
            return null
        }
    }
}
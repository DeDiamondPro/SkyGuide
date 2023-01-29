package dev.dediamondpro.skyguide.map.navigation

import cc.polyfrost.oneconfig.libs.universal.UChat
import dev.dediamondpro.skyguide.map.Island
import dev.dediamondpro.skyguide.map.SkyblockMap
import dev.dediamondpro.skyguide.map.poi.DestinationPoi
import net.minecraft.util.EnumChatFormatting
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

class NavigationHandler {

    @SubscribeEvent
    fun onRender(event: RenderWorldLastEvent) {
        val action = actions[SkyblockMap.getCurrentIsland() ?: return]
        if (!triedNavigation && action == null && destinationPio.destination != null) navigateTo(destinationPio.destination!!)
        if (action == null) return
        action.drawAction(event.partialTicks)
    }

    @SubscribeEvent
    fun onWorldSwap(event: WorldEvent.Load) {
        triedNavigation = false
    }

    companion object {
        private val actions = mutableMapOf<Island, NavigationAction>()
        private var triedNavigation = false
        val destinationPio = DestinationPoi(null)

        fun navigateTo(destination: Destination) {
            clearNavigation()
            destinationPio.destination = destination
            val currentIsland = SkyblockMap.getCurrentIsland() ?: return
            if (currentIsland != destination.island) {
                val route = findRouteToIsland(
                    destination.island.zone ?: return,
                    currentIsland,
                    mutableListOf()
                )
                if (route == null) {
                    UChat.chat("${EnumChatFormatting.RED}Could not find a route!")
                    triedNavigation = true
                    return
                }
                for (step in route) actions[step.key] = step.value.getAction(destination)
            }
            actions[destination.island] = DestinationAction(destination)
        }

        fun clearNavigation() {
            actions.clear()
            destinationPio.destination = null
        }

        private fun findRouteToIsland(
            destination: String,
            currentIsland: Island,
            visitedIslands: MutableList<String>
        ): MutableMap<Island, NavigationProvider>? {
            visitedIslands.add(currentIsland.zone!!)
            for (provider in getProviders(currentIsland)) {
                for (dest in provider.destinations) {
                    if (visitedIslands.contains(dest)) continue
                    if (dest == destination) return mutableMapOf(currentIsland to provider)
                    val destinationIsland = SkyblockMap.getIslandByZone(dest) ?: continue
                    val path = findRouteToIsland(destination, destinationIsland, visitedIslands) ?: continue
                    path[currentIsland] = provider
                    return path
                }
            }
            return null
        }

        private fun getProviders(currentIsland: Island): List<NavigationProvider> {
            return currentIsland.portals + currentIsland.npcs
        }
    }
}
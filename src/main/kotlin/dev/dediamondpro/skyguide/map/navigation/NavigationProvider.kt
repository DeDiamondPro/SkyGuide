package dev.dediamondpro.skyguide.map.navigation

interface NavigationProvider {
    val destinations: List<String>
    fun getAction(destination: Destination): NavigationAction
}
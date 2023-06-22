package dev.dediamondpro.skyguide.compat

import dev.dediamondpro.skyguide.map.poi.NEUWaypoint

interface INEUCompat {
    fun getCurrentlyTrackedWaypoint(): NEUWaypoint?
    fun useWarp()
    fun untrackWaypoint()

    companion object {
        val instance by lazy {
            try {
                @Suppress("DEPRECATION")
                NEUCompat::class.java.newInstance()
            } catch (e: Throwable) {
                // Need to catch throwable due to NoClassDefFoundError
                null
            }
        }
    }
}
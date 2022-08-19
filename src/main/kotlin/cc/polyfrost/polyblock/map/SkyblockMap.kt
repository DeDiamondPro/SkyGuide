package cc.polyfrost.polyblock.map

import cc.polyfrost.polyblock.utils.SBInfo
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.io.File
import java.nio.file.Files

object SkyblockMap {
    /**
     * format : <world id, <zone id, island data>>
     * Example of separate worlds: main (hub, ect...), winter (jerry's workshop)
     */
    var islands = mutableMapOf<String, MutableMap<String, Island>>()
    var zoneToWorld = mutableMapOf<String, MutableMap<String, Island>>()

    fun getCurrentIsland(): Island? {
        return zoneToWorld[SBInfo.zone]?.get(SBInfo.zone)
    }

    fun getCurrentWorld(): MutableMap<String, Island>? {
        return zoneToWorld[SBInfo.zone]
    }

    fun currentWorldAvailable(): Boolean {
        return zoneToWorld.containsKey(SBInfo.zone)
    }

    fun initialize(file: File): Boolean {
        try {
            Files.newInputStream(file.toPath()).use {
                islands = Json.decodeFromStream(it)
                for (world in islands.values) {
                    for (zone in world.keys) {
                        zoneToWorld[zone] = world
                    }
                }
                return true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }
}
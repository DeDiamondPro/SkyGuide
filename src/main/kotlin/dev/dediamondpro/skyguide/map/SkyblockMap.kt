package dev.dediamondpro.skyguide.map

import dev.dediamondpro.skyguide.map.poi.Searchable
import dev.dediamondpro.skyguide.utils.SBInfo
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.io.File
import java.nio.file.Files

object SkyblockMap {
    /**
     * format : <world id, <zone id, island data>>
     * Example of separate worlds: main (hub, ect...), winter (Jerry's workshop)
     */
    private val json = Json { ignoreUnknownKeys = true }
    var worlds = mutableMapOf<String, MutableMap<String, Island>>()
    private var zoneToWorld = mutableMapOf<String, MutableMap<String, Island>>()
    val searchables by lazy {
        val list = mutableListOf<Searchable>()
        for ((_, world) in worlds) {
            for ((_, island) in world) {
                list.add(island)
                list.addAll(island.npcs)
                list.addAll(island.portals.filter { it.command != null && it.name.isNotBlank() })
            }
        }
        return@lazy list
    }

    fun getCurrentIsland(): Island? {
        return zoneToWorld[SBInfo.zone]?.get(SBInfo.zone)
    }

    fun currentIslandAvailable(): Boolean {
        return currentWorldAvailable() && getCurrentWorld()!!.containsKey(SBInfo.zone)
    }

    fun getCurrentWorld(): MutableMap<String, Island>? {
        return zoneToWorld[SBInfo.zone]
    }

    fun currentWorldAvailable(): Boolean {
        return zoneToWorld.containsKey(SBInfo.zone)
    }

    fun isZoneInWorld(zone: String): Boolean {
        return currentWorldAvailable() && getCurrentWorld()!!.containsKey(zone)
    }

    fun getWorldByIsland(island: Island): MutableMap<String, Island>? {
        return worlds.values.firstOrNull { it.values.contains(island) }
    }

    fun getZoneByIsland(island: Island): String? {
        for (world in worlds.values) {
            if (!world.containsValue(island)) continue
            for ((zone, island2) in world) {
                if (island == island2) return zone
            }
        }
        return null
    }

    fun getIslandByZone(zone: String): Island? {
        val world = zoneToWorld[zone] ?: return null
        return world[zone]
    }

    fun initialize(file: File): Boolean {
        try {
            Files.newInputStream(file.toPath()).use {
                worlds = json.decodeFromStream(it)
                for (world in worlds.values) {
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
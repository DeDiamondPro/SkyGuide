package cc.polyfrost.polyblock.map

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.io.File
import java.nio.file.Files

object SkyblockMap {
    var islands = mutableMapOf<String, Island>()

    fun initialize() {
        try {
            Files.newInputStream(File("./config/PolyBlock/map.json").toPath()).use {
                islands = Json.decodeFromStream(it)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
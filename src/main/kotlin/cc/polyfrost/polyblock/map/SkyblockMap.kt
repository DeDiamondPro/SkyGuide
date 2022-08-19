package cc.polyfrost.polyblock.map

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.io.File
import java.nio.file.Files

object SkyblockMap {
    var islands = mutableMapOf<String, Island>()

    fun initialize(file: File): Boolean {
        try {
            Files.newInputStream(file.toPath()).use {
                islands = Json.decodeFromStream(it)
                return true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }
}
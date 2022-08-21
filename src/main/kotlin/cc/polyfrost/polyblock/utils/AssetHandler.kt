package cc.polyfrost.polyblock.utils

import cc.polyfrost.oneconfig.renderer.AssetLoader
import cc.polyfrost.oneconfig.utils.Multithreading
import cc.polyfrost.oneconfig.utils.NetworkUtils
import cc.polyfrost.polyblock.PolyBlock
import cc.polyfrost.polyblock.gui.DownloadWindow
import cc.polyfrost.polyblock.map.SkyblockMap
import org.lwjgl.nanovg.NanoVG
import java.io.File
import java.io.IOException
import java.net.URL
import java.nio.file.Files
import java.nio.file.StandardCopyOption

object AssetHandler {
    private val loadedAssets = mutableListOf<String>()

    fun loadAsset(vg: Long, fileName: String): Boolean {
        if (loadedAssets.contains(fileName)) return true
        if (AssetLoader.INSTANCE.loadImage(
                vg,
                fileName,
                NanoVG.NVG_IMAGE_GENERATE_MIPMAPS or NanoVG.NVG_IMAGE_NEAREST
            )
        ) {
            loadedAssets.add(fileName)
            return true
        }
        return false
    }

    fun unloadAssets(vg: Long) {
        for (image in loadedAssets) {
            AssetLoader.INSTANCE.removeImage(vg, image)
        }
        loadedAssets.clear()
    }

    fun initialize() {
        Multithreading.runAsync {
            val mapFile = "config/PolyBlock/map.json".toFile()
            val newMapFile = "config/PolyBlock/map-new.json".toFile()
            mapFile.parentFile.mkdirs()
            if ( // try to download and parse new data
                NetworkUtils.downloadFile("https://mods.polyfrost.cc/assets/polyblock/map.json", newMapFile)
                && SkyblockMap.initialize(newMapFile)
            ) {
                Files.move(newMapFile.toPath(), mapFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
            } else if (mapFile.exists()) { // if downloading or parsing failed use old data
                if (newMapFile.exists()) newMapFile.delete()
                SkyblockMap.initialize(mapFile)
            }

            updateTextures()
        }
    }

    fun updateTextures() {
        val imagesToUpdate = mutableMapOf<WebAsset, File>()
        for (world in SkyblockMap.worlds.values) {
            for (island in world.values) {
                val image = island.image
                val file = image.filePath.toFile()
                if (!file.exists() || image.getSha256() != IOUtils.getSha256(file)) {
                    image.initialized = false
                    imagesToUpdate[image] = file
                } else {
                    image.initialized = true
                }
            }
        }
        downloadAssets(imagesToUpdate)
    }

    private fun downloadAssets(assets: MutableMap<WebAsset, File>) {
        Multithreading.runAsync {
            DownloadWindow.setupDownload(assets.size)
            for ((asset, file) in assets) {
                if (file.exists() && !file.delete()) {
                    DownloadWindow.currentFile++
                    continue
                }
                file.parentFile.mkdirs()
                val con = URL(asset.getUrl()).openConnection()
                con.setRequestProperty("User-Agent", "PolyBlock-" + PolyBlock.VER)
                con.connectTimeout = 5000
                con.readTimeout = 5000
                val length = con.contentLength
                var downloadDone = false
                Multithreading.runAsync {
                    try {
                        con.getInputStream().use {
                            Files.copy(
                                it,
                                file.toPath(),
                                StandardCopyOption.REPLACE_EXISTING
                            )
                        }
                        if (file.exists()) asset.initialized = true
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                    downloadDone = true
                }
                while (!downloadDone) {
                    DownloadWindow.currentPercent = (file.length().toDouble() / length.toDouble()).toFloat()
                }
                DownloadWindow.currentPercent = 0f
                DownloadWindow.currentFile++
            }
            DownloadWindow.downloading = false
        }
    }
}
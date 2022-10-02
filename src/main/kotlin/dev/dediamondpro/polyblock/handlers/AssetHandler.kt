package dev.dediamondpro.polyblock.handlers

import dev.dediamondpro.polyblock.PolyBlock
import dev.dediamondpro.polyblock.config.BlockConfig
import dev.dediamondpro.polyblock.map.SkyblockMap
import dev.dediamondpro.polyblock.utils.*
import gg.essential.api.utils.Multithreading
import gg.essential.universal.UChat
import gg.essential.universal.UGraphics
import gg.essential.universal.UMinecraft
import gg.essential.universal.utils.ReleasedDynamicTexture
import java.io.File
import java.io.IOException
import java.net.URL
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import javax.imageio.ImageIO


object AssetHandler {
    private val loadedAssets = mutableMapOf<String, ReleasedDynamicTexture>()
    private var currentPercent = 0f
    private var currentFile = 0
    private var totalFiles = 0
    private var ticks = 0
    var downloadedAssets = false

    private fun setupDownload(assets: Int) {
        totalFiles = assets
        currentFile = 0
        currentPercent = 0f
        if (UMinecraft.getWorld() == null) return
        postMessage()
    }

    private fun postMessage() {
        val percent = (currentFile.toFloat() + currentPercent) / totalFiles.toFloat()
        if (percent == 1f) {
            UChat.chat("${PolyBlock.NAME} > Finished downloading assets!")
            downloadedAssets = true
        } else {
            UChat.chat("${PolyBlock.NAME} > Downloading assets... ${(percent * 100).toInt()}% ($currentFile/$totalFiles)")
            TickDelay(20, AssetHandler::postMessage)
        }
    }

    fun loadAsset(fileName: String): Boolean {
        if (loadedAssets.containsKey(fileName)) return true
        try {
            val texture =
                if (fileName.startsWith("/assets/"))
                    UGraphics.getTexture(this.javaClass.getResourceAsStream(fileName)) ?: return false
                else UGraphics.getTexture(ImageIO.read(fileName.toFile())) ?: return false
            texture.uploadTexture()
            loadedAssets[fileName] = texture
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    fun unloadAsset(fileName: String) {
        loadedAssets[fileName]?.deleteGlTexture()
        loadedAssets.remove(fileName)
    }

    fun unloadAssets() {
        loadedAssets.keys.removeIf {
            unloadAsset(it)
            true
        }
    }

    fun getAsset(fileName: String): Int {
        return loadedAssets[fileName]!!.glTextureId
    }

    fun initialize() {
        downloadedAssets = true
        Multithreading.runAsync {
            val mapFile = "config/${PolyBlock.ID}/map.json".toFile()
            val newMapFile = "config/${PolyBlock.ID}/map-new.json".toFile()
            mapFile.parentFile.mkdirs()
            if ( // try to download and parse new data
                BlockConfig.downloadAssets
                && NetworkUtils.downloadFile("https://api.dediamondpro.dev/assets/polyblock/map.json", newMapFile)
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
                for (image in island.images.values) {
                    val file = image.filePath.toFile()
                    if ((!file.exists() || image.getSha256() != IOUtils.getSha256(file)) && BlockConfig.downloadAssets) {
                        image.initialized = false
                        imagesToUpdate[image] = file
                        unloadAsset(image.filePath)
                    } else {
                        image.initialized = true
                    }
                }
            }
        }
        downloadAssets(imagesToUpdate)
    }

    private fun downloadAssets(assets: MutableMap<WebAsset, File>) {
        if (assets.isEmpty()) return
        Multithreading.runAsync {
            setupDownload(assets.size)
            for ((asset, file) in assets) {
                if (file.exists() && !file.delete()) {
                    currentFile++
                    continue
                }
                file.parentFile.mkdirs()
                val con = NetworkUtils.setupConnection(URL(asset.getUrl()))
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
                    currentPercent = (file.length().toDouble() / length.toDouble()).toFloat()
                }
                currentPercent = 0f
                currentFile++
            }
            if (!BlockConfig.lazyLoading && BlockConfig.keepAssetsLoaded) {
                TickDelay(0) {
                    for (file in assets.values) loadAsset(file.path)
                }
            }
        }
    }
}
package cc.polyfrost.polyblock.utils

import cc.polyfrost.oneconfig.renderer.AssetLoader
import cc.polyfrost.polyblock.config.BlockConfig
import org.lwjgl.nanovg.NanoVG

object AssetHandler {
    private val loadedAssets = mutableListOf<String>()

    fun loadAsset(vg: Long, fileName: String): Boolean {
        if (loadedAssets.contains(fileName)) return true
        var flags = NanoVG.NVG_IMAGE_GENERATE_MIPMAPS
        if (!BlockConfig.smooth || fileName == "/assets/polyblock/player.png") flags = flags or NanoVG.NVG_IMAGE_NEAREST
        if (AssetLoader.INSTANCE.loadImage(vg, fileName, flags)) {
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
}
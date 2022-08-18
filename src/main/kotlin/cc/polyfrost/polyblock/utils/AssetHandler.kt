package cc.polyfrost.polyblock.utils

import cc.polyfrost.oneconfig.renderer.AssetLoader

object AssetHandler {
    private val loadedAssets = mutableListOf<String>()

    fun loadAsset(vg: Long, fileName: String) {
        if (loadedAssets.contains(fileName)) return
        if (AssetLoader.INSTANCE.loadImage(vg, fileName)) {
            loadedAssets.add(fileName) //TODO: NanoVG.NVG_IMAGE_NEAREST or NanoVG.NVG_IMAGE_GENERATE_MIPMAPS
        }
    }

    fun unloadAssets(vg: Long) {
        for (image in loadedAssets) {
            AssetLoader.INSTANCE.removeImage(vg, image)
        }
        loadedAssets.clear()
    }
}
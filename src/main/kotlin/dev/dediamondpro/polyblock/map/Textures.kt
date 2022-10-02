package dev.dediamondpro.polyblock.map

import dev.dediamondpro.polyblock.PolyBlock
import dev.dediamondpro.polyblock.config.BlockConfig
import dev.dediamondpro.polyblock.utils.RenderUtils
import dev.dediamondpro.polyblock.utils.WebAsset

@kotlinx.serialization.Serializable
data class Textures(val low: ShaImage, val medium: ShaImage, val high: ShaImage, val zoom: Float = 1f) : WebAsset {
    val filePath: String = "config/${PolyBlock.ID}/assets/" + getUrl().split("/")[getUrl().split("/").size - 1]
    override var initialized: Boolean = false

    override fun getUrl(): String {
        return when (BlockConfig.textureQuality) {
            0 -> low.url
            1 -> medium.url
            2 -> high.url
            else -> medium.url
        }
    }

    fun getSha256(): String {
        return when (BlockConfig.textureQuality) {
            0 -> low.sha256
            1 -> medium.sha256
            2 -> high.sha256
            else -> medium.sha256
        }
    }

    fun draw(x: Number, y: Number, width: Number, height: Number) {
        if (!initialized) return
        RenderUtils.drawImage(filePath, x, y, width, height)
    }
}

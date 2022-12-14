package dev.dediamondpro.skyguide.map

import dev.dediamondpro.skyguide.SkyGuide
import dev.dediamondpro.skyguide.config.Config
import dev.dediamondpro.skyguide.utils.RenderUtils
import dev.dediamondpro.skyguide.utils.WebAsset
import org.lwjgl.opengl.GL11

@kotlinx.serialization.Serializable
data class Textures(
    val low: ShaImage,
    val medium: ShaImage,
    val high: ShaImage,
    val underground: Boolean = false,
    private val condition: String = ""
) : WebAsset {
    val filePath: String = "config/${SkyGuide.ID}/assets/" + getUrl().split("/")[getUrl().split("/").size - 1]
    val parsedCondition = Condition(condition)
    override var initialized: Boolean = false

    override fun getUrl(): String {
        return when (Config.textureQuality) {
            0 -> low.url
            1 -> medium.url
            2 -> high.url
            else -> medium.url
        }
    }

    fun getSha256(): String {
        return when (Config.textureQuality) {
            0 -> low.sha256
            1 -> medium.sha256
            2 -> high.sha256
            else -> medium.sha256
        }
    }

    fun draw(x: Number, y: Number, width: Number, height: Number, filter: Int = GL11.GL_NEAREST) {
        if (!initialized) return
        RenderUtils.drawImage(filePath, x, y, width, height, filter)
    }
}

package dev.dediamondpro.skyguide.map.poi

import dev.dediamondpro.skyguide.map.Island
import dev.dediamondpro.skyguide.utils.RenderUtils
import gg.essential.universal.UMinecraft
import gg.essential.universal.UResolution
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlin.math.pow
import kotlin.math.sqrt

@Serializable
abstract class PointOfInterest {
    abstract val x: Float
    abstract val y: Float
    abstract val z: Float

    @Transient
    open var island: Island? = null

    protected abstract fun shouldDraw(): Boolean

    fun shouldDraw(locations: MutableList<Pair<Float, Float>>, scale: Float): Boolean {
        for (location in locations) {
            val dist = sqrt(
                (location.first * scale - x * scale).toDouble()
                    .pow(2.0) + (location.second * scale - z * scale).toDouble().pow(2.0)
            )
            if (dist < 32) return false
        }
        return shouldDraw()
    }

    fun draw(xMove: Float, yMove: Float, xOffset: Float, yOffset: Float, scale: Float) {
        drawBackground((x + xOffset + xMove) * scale, (z + yOffset + yMove) * scale)
        drawIcon((x + xOffset + xMove) * scale, (z + yOffset + yMove) * scale)
    }

    protected open fun drawBackground(x: Float, y: Float) {
        RenderUtils.drawImage(
            "/assets/skyguide/map_location.png",
            x - 16f,
            y - 16,
            32f,
            32f
        )
    }

    protected abstract fun drawIcon(x: Float, y: Float)

    fun shouldDrawTooltip(
        mouseXScaled: Float,
        mouseYScaled: Float,
        xOffset: Float,
        yOffset: Float,
        scale: Float
    ): Boolean {
        return shouldDraw()
                && mouseXScaled >= (x + xOffset - 16f / scale) && mouseXScaled <= (x + xOffset + 16f / scale)
                && mouseYScaled >= (z + yOffset - 16f / scale) && mouseYScaled <= (z + yOffset + 16f / scale)
    }

    fun drawTooltip(mouseX: Int, mouseY: Int) {
        RenderUtils.drawToolTip(
            getTooltip(),
            mouseX,
            mouseY,
            UResolution.windowWidth,
            UResolution.windowHeight,
            400,
            UMinecraft.getFontRenderer()
        )
    }

    abstract fun getTooltip(): List<String>

    abstract fun onLeftClick()

    abstract fun onRightClick()
}
package dev.dediamondpro.skyguide.map.poi

import cc.polyfrost.oneconfig.libs.universal.UGraphics
import cc.polyfrost.oneconfig.libs.universal.UMinecraft
import cc.polyfrost.oneconfig.libs.universal.UResolution
import dev.dediamondpro.skyguide.map.Island
import dev.dediamondpro.skyguide.utils.RenderUtils
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

    fun shouldDraw(locations: MutableList<Pair<Float, Float>>, scale: Float, poiScale: Float): Boolean {
        for (location in locations) {
            val dist = sqrt(
                (location.first * scale - x * scale).toDouble()
                    .pow(2.0) + (location.second * scale - z * scale).toDouble().pow(2.0)
            )
            if (dist < 32 * poiScale) return false
        }
        return shouldDraw()
    }

    fun draw(xMove: Float, yMove: Float, xOffset: Float, yOffset: Float, scale: Float, poiScale: Float) {
        drawBackground((x + xOffset + xMove) * scale, (z + yOffset + yMove) * scale, poiScale)
        drawIcon((x + xOffset + xMove) * scale, (z + yOffset + yMove) * scale, poiScale)
    }

    fun drawRaw(x: Float, y: Float, poiScale: Float) {
        drawBackground(x, y, poiScale)
        drawIcon(x, y, poiScale)
    }

    protected open fun drawBackground(x: Float, y: Float, scale: Float) {
        UGraphics.color4f(1f, 1f, 1f, 1f)
        RenderUtils.drawImage(
            "/assets/skyguide/map_location.png",
            x - 16f * scale,
            y - 16 * scale,
            32f * scale,
            32f * scale
        )
    }

    protected abstract fun drawIcon(x: Float, y: Float, scale: Float)

    fun shouldDrawTooltip(
        mouseXScaled: Float,
        mouseYScaled: Float,
        xOffset: Float,
        yOffset: Float,
        scale: Float,
        poiScale: Float
    ): Boolean {
        return shouldDraw()
                && mouseXScaled >= (x + xOffset - (16f * poiScale) / scale) && mouseXScaled <= (x + xOffset + (16f * poiScale) / scale)
                && mouseYScaled >= (z + yOffset - (16f * poiScale) / scale) && mouseYScaled <= (z + yOffset + (16f * poiScale) / scale)
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
package dev.dediamondpro.skyguide.map.poi

import dev.dediamondpro.skyguide.utils.RenderUtils
import gg.essential.universal.UMinecraft
import gg.essential.universal.UResolution
import kotlinx.serialization.Serializable

@Serializable
abstract class PointOfInterest {
    abstract val x: Float
    abstract val y: Float
    abstract val z: Float

    abstract fun shouldDraw(): Boolean

    fun draw(xOffset: Float, yOffset: Float, scale: Float) {
        drawBackground(x + xOffset, z + yOffset, scale)
        drawIcon(x + xOffset, z + yOffset, scale)
    }

    private fun drawBackground(x: Float, y: Float, scale: Float) {
        RenderUtils.drawImage(
            "/assets/skyguide/map_location.png",
            x - 16f / scale,
            y - 16f / scale,
            32f / scale,
            32f / scale
        )
    }

    protected abstract fun drawIcon(x: Float, y: Float, scale: Float)

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
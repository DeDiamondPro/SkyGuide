package dev.dediamondpro.skyguide.gui

import cc.polyfrost.oneconfig.libs.universal.UGraphics
import cc.polyfrost.oneconfig.libs.universal.UMatrixStack
import dev.dediamondpro.skyguide.utils.GuiUtils
import dev.dediamondpro.skyguide.utils.RenderUtils
import java.awt.Color
import java.util.function.Consumer
import kotlin.math.pow

class Button(
    val text: String,
    var x: Float,
    var y: Float,
    private val height: Float,
    private val clickAction: Consumer<Button>
) {
    val width = UGraphics.getStringWidth(text).toFloat() + 16f
    private var whitePercent = 0f

    fun draw(matrixStack: UMatrixStack, mouseX: Int, mouseY: Int): Boolean {
        val hovering = mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height
        val clicked = hovering && GuiUtils.leftClicked
        if (clicked) clickAction.accept(this)
        if (whitePercent != 1f && hovering) whitePercent += GuiUtils.getGuiDeltaTime() / 100f
        else if (whitePercent != 0f) whitePercent -= GuiUtils.getGuiDeltaTime() / 100f
        whitePercent = whitePercent.coerceAtLeast(0f).coerceAtMost(1f)
        RenderUtils.drawRect(x, y, width, height, Color(1f, 1f, 1f, 0.6f * easeInOutQuad(whitePercent)).rgb)
        UGraphics.drawString(matrixStack, text, x + 8f, y + height / 2f - 3f, Color(255, 255, 255).rgb, true)
        return false
    }

    private fun easeInOutQuad(x: Float): Float {
        return if (x < 0.5f) 2f * x * x else 1f - (-2f * x + 2f).pow(2f) / 2f
    }

    override fun toString(): String {
        return "text: $text, x: $x, y: $y, width: $width, height: $height"
    }
}
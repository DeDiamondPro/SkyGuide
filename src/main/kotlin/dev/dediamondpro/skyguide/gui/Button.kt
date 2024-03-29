package dev.dediamondpro.skyguide.gui

import dev.dediamondpro.skyguide.utils.GuiUtils
import dev.dediamondpro.skyguide.utils.RenderUtils
import gg.essential.universal.UGraphics
import gg.essential.universal.UMatrixStack
import java.awt.Color
import java.util.function.Consumer
import kotlin.math.pow

class Button(
    val text: String?,
    var x: Float,
    var y: Float,
    private val height: Float,
    private val icon: String? = null,
    private val clickAction: Consumer<Button>
) {
    val width = if (text != null) UGraphics.getStringWidth(text).toFloat() + 16f else height
    private var whitePercent = 0f

    fun draw(matrixStack: UMatrixStack, mouseX: Int, mouseY: Int, canPress: Boolean): Boolean {
        val hovering = mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height && canPress
        val clicked = hovering && GuiUtils.leftClicked
        if (clicked) clickAction.accept(this)
        if (whitePercent != 1f && hovering) whitePercent += GuiUtils.getGuiDeltaTime() / 100f
        else if (whitePercent != 0f) whitePercent -= GuiUtils.getGuiDeltaTime() / 100f
        whitePercent = whitePercent.coerceAtLeast(0f).coerceAtMost(1f)
        RenderUtils.drawRect(x, y, width, height, Color(1f, 1f, 1f, 0.6f * easeInOutQuad(whitePercent)).rgb)
        if (text != null) {
            UGraphics.drawString(matrixStack, text, x + 8f, y + height / 2f - 3f, Color(255, 255, 255).rgb, true)
        }
        if (icon != null) {
            UGraphics.color4f(1f, 1f, 1f, 1f)
            RenderUtils.drawImage(icon, x + width / 2f - 8f, y + height / 2f - 8f, 16f, 16f)
        }
        return false
    }

    private fun easeInOutQuad(x: Float): Float {
        return if (x < 0.5f) 2f * x * x else 1f - (-2f * x + 2f).pow(2f) / 2f
    }

    override fun toString(): String {
        return "text: $text, x: $x, y: $y, width: $width, height: $height"
    }
}
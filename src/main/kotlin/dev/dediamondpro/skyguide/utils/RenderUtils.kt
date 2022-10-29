package dev.dediamondpro.skyguide.utils

import dev.dediamondpro.skyguide.handlers.AssetHandler
import gg.essential.universal.UGraphics
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.gui.Gui
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.entity.Entity
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumChatFormatting
import net.minecraft.util.MathHelper
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL14
import javax.vecmath.Vector3f
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt


object RenderUtils {
    private val beaconBeam = ResourceLocation("textures/entity/beacon_beam.png")

    /**
     * Adapted from NotEnoughUpdates under LGPL license https://github.com/NotEnoughUpdates/NotEnoughUpdates/blob/master/COPYING.LESSER
     */
    fun renderBeaconBeam(block: BlockPos, rgb: Int, partialTicks: Float) {
        val viewer: Entity = Minecraft.getMinecraft().renderViewEntity
        val viewerX = viewer.lastTickPosX + (viewer.posX - viewer.lastTickPosX) * partialTicks
        val viewerY = viewer.lastTickPosY + (viewer.posY - viewer.lastTickPosY) * partialTicks
        val viewerZ = viewer.lastTickPosZ + (viewer.posZ - viewer.lastTickPosZ) * partialTicks
        val x = block.x - viewerX
        val y = block.y - viewerY
        val z = block.z - viewerZ
        renderBeaconBeam(x, y, z, rgb, partialTicks)
    }


    /**
     * Adapted from NotEnoughUpdates under LGPL license https://github.com/NotEnoughUpdates/NotEnoughUpdates/blob/master/COPYING.LESSER
     */
    private fun renderBeaconBeam(
        x: Double, y: Double, z: Double, rgb: Int,
        partialTicks: Float, disableDepth: Boolean = false
    ) {
        val height = 300
        val bottomOffset = 0
        val topOffset = bottomOffset + height
        val tessellator = Tessellator.getInstance()
        val worldRenderer = tessellator.worldRenderer
        if (disableDepth) {
            GlStateManager.disableDepth()
        }
        Minecraft.getMinecraft().textureManager.bindTexture(beaconBeam)
        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT.toFloat())
        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT.toFloat())
        GlStateManager.disableLighting()
        GlStateManager.enableCull()
        GlStateManager.enableTexture2D()
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ONE, GL11.GL_ZERO)
        GlStateManager.enableBlend()
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO)
        val time = Minecraft.getMinecraft().theWorld.totalWorldTime + partialTicks.toDouble()
        val d1 = MathHelper.func_181162_h(-time * 0.2 - MathHelper.floor_double(-time * 0.1).toDouble())
        val r = (rgb shr 16 and 0xFF) / 255f
        val g = (rgb shr 8 and 0xFF) / 255f
        val b = (rgb and 0xFF) / 255f
        val d2 = time * 0.025 * -1.5
        val d4 = 0.5 + cos(d2 + 2.356194490192345) * 0.2
        val d5 = 0.5 + sin(d2 + 2.356194490192345) * 0.2
        val d6 = 0.5 + cos(d2 + Math.PI / 4.0) * 0.2
        val d7 = 0.5 + sin(d2 + Math.PI / 4.0) * 0.2
        val d8 = 0.5 + cos(d2 + 3.9269908169872414) * 0.2
        val d9 = 0.5 + sin(d2 + 3.9269908169872414) * 0.2
        val d10 = 0.5 + cos(d2 + 5.497787143782138) * 0.2
        val d11 = 0.5 + sin(d2 + 5.497787143782138) * 0.2
        val d14 = -1.0 + d1
        val d15 = height.toDouble() * 2.5 + d14
        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR)
        worldRenderer.pos(x + d4, y + topOffset, z + d5).tex(1.0, d15).color(r, g, b, 1.0f * 1f).endVertex()
        worldRenderer.pos(x + d4, y + bottomOffset, z + d5).tex(1.0, d14).color(r, g, b, 1.0f).endVertex()
        worldRenderer.pos(x + d6, y + bottomOffset, z + d7).tex(0.0, d14).color(r, g, b, 1.0f).endVertex()
        worldRenderer.pos(x + d6, y + topOffset, z + d7).tex(0.0, d15).color(r, g, b, 1.0f * 1f).endVertex()
        worldRenderer.pos(x + d10, y + topOffset, z + d11).tex(1.0, d15).color(r, g, b, 1.0f * 1f).endVertex()
        worldRenderer.pos(x + d10, y + bottomOffset, z + d11).tex(1.0, d14).color(r, g, b, 1.0f).endVertex()
        worldRenderer.pos(x + d8, y + bottomOffset, z + d9).tex(0.0, d14).color(r, g, b, 1.0f).endVertex()
        worldRenderer.pos(x + d8, y + topOffset, z + d9).tex(0.0, d15).color(r, g, b, 1.0f * 1f).endVertex()
        worldRenderer.pos(x + d6, y + topOffset, z + d7).tex(1.0, d15).color(r, g, b, 1.0f * 1f).endVertex()
        worldRenderer.pos(x + d6, y + bottomOffset, z + d7).tex(1.0, d14).color(r, g, b, 1.0f).endVertex()
        worldRenderer.pos(x + d10, y + bottomOffset, z + d11).tex(0.0, d14).color(r, g, b, 1.0f).endVertex()
        worldRenderer.pos(x + d10, y + topOffset, z + d11).tex(0.0, d15).color(r, g, b, 1.0f * 1f).endVertex()
        worldRenderer.pos(x + d8, y + topOffset, z + d9).tex(1.0, d15).color(r, g, b, 1.0f * 1f).endVertex()
        worldRenderer.pos(x + d8, y + bottomOffset, z + d9).tex(1.0, d14).color(r, g, b, 1.0f).endVertex()
        worldRenderer.pos(x + d4, y + bottomOffset, z + d5).tex(0.0, d14).color(r, g, b, 1.0f).endVertex()
        worldRenderer.pos(x + d4, y + topOffset, z + d5).tex(0.0, d15).color(r, g, b, 1.0f * 1f).endVertex()
        tessellator.draw()
        GlStateManager.disableCull()
        val d12 = -1.0 + d1
        val d13 = height + d12
        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR)
        worldRenderer.pos(x + 0.2, y + topOffset, z + 0.2).tex(1.0, d13).color(r, g, b, 0.25f * 1f).endVertex()
        worldRenderer.pos(x + 0.2, y + bottomOffset, z + 0.2).tex(1.0, d12).color(r, g, b, 0.25f).endVertex()
        worldRenderer.pos(x + 0.8, y + bottomOffset, z + 0.2).tex(0.0, d12).color(r, g, b, 0.25f).endVertex()
        worldRenderer.pos(x + 0.8, y + topOffset, z + 0.2).tex(0.0, d13).color(r, g, b, 0.25f * 1f).endVertex()
        worldRenderer.pos(x + 0.8, y + topOffset, z + 0.8).tex(1.0, d13).color(r, g, b, 0.25f * 1f).endVertex()
        worldRenderer.pos(x + 0.8, y + bottomOffset, z + 0.8).tex(1.0, d12).color(r, g, b, 0.25f).endVertex()
        worldRenderer.pos(x + 0.2, y + bottomOffset, z + 0.8).tex(0.0, d12).color(r, g, b, 0.25f).endVertex()
        worldRenderer.pos(x + 0.2, y + topOffset, z + 0.8).tex(0.0, d13).color(r, g, b, 0.25f * 1f).endVertex()
        worldRenderer.pos(x + 0.8, y + topOffset, z + 0.2).tex(1.0, d13).color(r, g, b, 0.25f * 1f).endVertex()
        worldRenderer.pos(x + 0.8, y + bottomOffset, z + 0.2).tex(1.0, d12).color(r, g, b, 0.25f).endVertex()
        worldRenderer.pos(x + 0.8, y + bottomOffset, z + 0.8).tex(0.0, d12).color(r, g, b, 0.25f).endVertex()
        worldRenderer.pos(x + 0.8, y + topOffset, z + 0.8).tex(0.0, d13).color(r, g, b, 0.25f * 1f).endVertex()
        worldRenderer.pos(x + 0.2, y + topOffset, z + 0.8).tex(1.0, d13).color(r, g, b, 0.25f * 1f).endVertex()
        worldRenderer.pos(x + 0.2, y + bottomOffset, z + 0.8).tex(1.0, d12).color(r, g, b, 0.25f).endVertex()
        worldRenderer.pos(x + 0.2, y + bottomOffset, z + 0.2).tex(0.0, d12).color(r, g, b, 0.25f).endVertex()
        worldRenderer.pos(x + 0.2, y + topOffset, z + 0.2).tex(0.0, d13).color(r, g, b, 0.25f * 1f).endVertex()
        tessellator.draw()
        GlStateManager.disableLighting()
        GlStateManager.enableTexture2D()
        if (disableDepth) {
            GlStateManager.enableDepth()
        }
    }

    /**
     * Adapted from NotEnoughUpdates under LGPL license https://github.com/NotEnoughUpdates/NotEnoughUpdates/blob/master/COPYING.LESSER
     */
    fun renderWayPoint(text: MutableList<String?>, loc: Vector3f, partialTicks: Float) {
        var lines = text
        GlStateManager.alphaFunc(516, 0.1f)
        GlStateManager.pushMatrix()
        val viewer = Minecraft.getMinecraft().renderViewEntity
        val viewerX = viewer.lastTickPosX + (viewer.posX - viewer.lastTickPosX) * partialTicks
        val viewerY = viewer.lastTickPosY + (viewer.posY - viewer.lastTickPosY) * partialTicks
        val viewerZ = viewer.lastTickPosZ + (viewer.posZ - viewer.lastTickPosZ) * partialTicks
        var x: Double = loc.x - viewerX
        var y: Double = loc.y - viewerY - viewer.eyeHeight
        var z: Double = loc.z - viewerZ
        val distSq = x * x + y * y + z * z
        val dist = sqrt(distSq)
        if (distSq > 144) {
            x *= 12 / dist
            y *= 12 / dist
            z *= 12 / dist
        }
        GlStateManager.translate(x, y, z)
        GlStateManager.translate(0f, viewer.eyeHeight, 0f)
        lines = ArrayList(lines)
        lines.add(EnumChatFormatting.YELLOW.toString() + dist.roundToInt() + "m")
        renderNametag(lines)
        GlStateManager.popMatrix()
        GlStateManager.disableLighting()
    }

    /**
     * Adapted from NotEnoughUpdates under LGPL license https://github.com/NotEnoughUpdates/NotEnoughUpdates/blob/master/COPYING.LESSER
     */
    fun renderNametag(lines: List<String?>) {
        val fontrenderer = Minecraft.getMinecraft().fontRendererObj
        val f = 1.6f
        val f1 = 0.016666668f * f
        GlStateManager.pushMatrix()
        GL11.glNormal3f(0.0f, 1.0f, 0.0f)
        GlStateManager.rotate(-Minecraft.getMinecraft().renderManager.playerViewY, 0.0f, 1.0f, 0.0f)
        GlStateManager.rotate(Minecraft.getMinecraft().renderManager.playerViewX, 1.0f, 0.0f, 0.0f)
        GlStateManager.scale(-f1, -f1, f1)
        GlStateManager.disableLighting()
        GlStateManager.depthMask(false)
        GlStateManager.disableDepth()
        GlStateManager.enableBlend()
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)
        val tessellator = Tessellator.getInstance()
        val worldrenderer = tessellator.worldRenderer
        val i = 0
        for (str in lines) {
            val j = fontrenderer.getStringWidth(str) / 2
            GlStateManager.disableTexture2D()
            worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR)
            worldrenderer.pos((-j - 1).toDouble(), (-1 + i).toDouble(), 0.0).color(0.0f, 0.0f, 0.0f, 0.25f).endVertex()
            worldrenderer.pos((-j - 1).toDouble(), (8 + i).toDouble(), 0.0).color(0.0f, 0.0f, 0.0f, 0.25f).endVertex()
            worldrenderer.pos((j + 1).toDouble(), (8 + i).toDouble(), 0.0).color(0.0f, 0.0f, 0.0f, 0.25f).endVertex()
            worldrenderer.pos((j + 1).toDouble(), (-1 + i).toDouble(), 0.0).color(0.0f, 0.0f, 0.0f, 0.25f).endVertex()
            tessellator.draw()
            GlStateManager.enableTexture2D()
            fontrenderer.drawString(str, -fontrenderer.getStringWidth(str) / 2, i, 553648127)
            GlStateManager.depthMask(true)
            fontrenderer.drawString(str, -fontrenderer.getStringWidth(str) / 2, i, -1)
            GlStateManager.translate(0f, 10f, 0f)
        }
        GlStateManager.enableDepth()
        GlStateManager.enableBlend()
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
        GlStateManager.popMatrix()
    }

    fun drawImage(fileName: String, x: Number, y: Number, width: Number, height: Number) {
        drawImage(fileName, x, y, width, height, GL11.GL_NEAREST)
    }

    fun drawImage(fileName: String, x: Number, y: Number, width: Number, height: Number, filter: Int) {
        if (!AssetHandler.loadAsset(fileName)) return
        UGraphics.bindTexture(AssetHandler.getAsset(fileName))
        UGraphics.enableTexture2D()
        UGraphics.enableBlend()
        UGraphics.tryBlendFuncSeparate(
            GL11.GL_SRC_ALPHA,
            GL11.GL_ONE_MINUS_SRC_ALPHA,
            GL11.GL_ONE,
            GL11.GL_ONE_MINUS_SRC_ALPHA
        )

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, filter)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, filter)

        val tessellator = Tessellator.getInstance()
        val worldrenderer = tessellator.worldRenderer
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX)
        worldrenderer
            .pos(x.toDouble(), y.toDouble() + height.toDouble(), 0.0)
            .tex(0.0, 1.0).endVertex()
        worldrenderer
            .pos(x.toDouble() + width.toDouble(), y.toDouble() + height.toDouble(), 0.0)
            .tex(1.0, 1.0).endVertex()
        worldrenderer
            .pos(x.toDouble() + width.toDouble(), y.toDouble(), 0.0)
            .tex(1.0, 0.0).endVertex()
        worldrenderer
            .pos(x.toDouble(), y.toDouble(), 0.0)
            .tex(0.0, 0.0).endVertex()
        tessellator.draw()

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST)

        UGraphics.disableBlend()
    }

    fun drawRect(x: Number, y: Number, width: Number, height: Number, color: Int) {
        Gui.drawRect(x.toInt(), y.toInt(), x.toInt() + width.toInt(), y.toInt() + height.toInt(), color)
    }

    /**
     * Adapted from NotEnoughUpdates under LGPL license https://github.com/NotEnoughUpdates/NotEnoughUpdates/blob/master/COPYING.LESSER
     */
    fun drawToolTip(
        text: List<String?>,
        mouseX: Int,
        mouseY: Int,
        screenWidth: Int,
        screenHeight: Int,
        maxTextWidth: Int,
        font: FontRenderer
    ) {
        var textLines = text
        if (textLines.isNotEmpty()) {
            GlStateManager.disableRescaleNormal()
            RenderHelper.disableStandardItemLighting()
            GlStateManager.disableLighting()
            GlStateManager.disableDepth()
            var tooltipTextWidth = 0
            for (textLine in textLines) {
                val textLineWidth = font.getStringWidth(textLine)
                if (textLineWidth > tooltipTextWidth) {
                    tooltipTextWidth = textLineWidth
                }
            }
            var needsWrap = false
            var titleLinesCount = 1
            var tooltipX = mouseX + 12
            if (tooltipX + tooltipTextWidth + 4 > screenWidth) {
                tooltipX = mouseX - 16 - tooltipTextWidth
                if (tooltipX < 4) { // if the tooltip doesn't fit on the screen
                    tooltipTextWidth = if (mouseX > screenWidth / 2) {
                        mouseX - 12 - 8
                    } else {
                        screenWidth - 16 - mouseX
                    }
                    needsWrap = true
                }
            }
            if (maxTextWidth in 1 until tooltipTextWidth) {
                tooltipTextWidth = maxTextWidth
                needsWrap = true
            }
            if (needsWrap) {
                var wrappedTooltipWidth = 0
                val wrappedTextLines: MutableList<String?> = ArrayList()
                for (i in textLines.indices) {
                    val textLine = textLines[i]
                    val wrappedLine = font.listFormattedStringToWidth(textLine, tooltipTextWidth)
                    if (i == 0) {
                        titleLinesCount = wrappedLine.size
                    }
                    for (line in wrappedLine) {
                        val lineWidth = font.getStringWidth(line)
                        if (lineWidth > wrappedTooltipWidth) {
                            wrappedTooltipWidth = lineWidth
                        }
                        wrappedTextLines.add(line)
                    }
                }
                tooltipTextWidth = wrappedTooltipWidth
                textLines = wrappedTextLines
                tooltipX = if (mouseX > screenWidth / 2) {
                    mouseX - 16 - tooltipTextWidth
                } else {
                    mouseX + 12
                }
            }
            var tooltipY = mouseY - 12
            var tooltipHeight = 8
            if (textLines.size > 1) {
                tooltipHeight += (textLines.size - 1) * 10
                if (textLines.size > titleLinesCount) {
                    tooltipHeight += 2 // gap between title lines and next lines
                }
            }
            if (tooltipY + tooltipHeight + 6 > screenHeight) {
                tooltipY = screenHeight - tooltipHeight - 6
            }
            val zLevel = 300
            val backgroundColor = -0xfeffff0
            drawGradientRect(
                zLevel,
                tooltipX - 3,
                tooltipY - 4,
                tooltipX + tooltipTextWidth + 3,
                tooltipY - 3,
                backgroundColor,
                backgroundColor
            )
            drawGradientRect(
                zLevel,
                tooltipX - 3,
                tooltipY + tooltipHeight + 3,
                tooltipX + tooltipTextWidth + 3,
                tooltipY + tooltipHeight + 4,
                backgroundColor,
                backgroundColor
            )
            drawGradientRect(
                zLevel,
                tooltipX - 3,
                tooltipY - 3,
                tooltipX + tooltipTextWidth + 3,
                tooltipY + tooltipHeight + 3,
                backgroundColor,
                backgroundColor
            )
            drawGradientRect(
                zLevel,
                tooltipX - 4,
                tooltipY - 3,
                tooltipX - 3,
                tooltipY + tooltipHeight + 3,
                backgroundColor,
                backgroundColor
            )
            drawGradientRect(
                zLevel,
                tooltipX + tooltipTextWidth + 3,
                tooltipY - 3,
                tooltipX + tooltipTextWidth + 4,
                tooltipY + tooltipHeight + 3,
                backgroundColor,
                backgroundColor
            )
            val borderColorStart = 0x505000FF
            val borderColorEnd = borderColorStart and 0xFEFEFE shr 1 or (borderColorStart and -0x1000000)
            drawGradientRect(
                zLevel,
                tooltipX - 3,
                tooltipY - 3 + 1,
                tooltipX - 3 + 1,
                tooltipY + tooltipHeight + 3 - 1,
                borderColorStart,
                borderColorEnd
            )
            drawGradientRect(
                zLevel,
                tooltipX + tooltipTextWidth + 2,
                tooltipY - 3 + 1,
                tooltipX + tooltipTextWidth + 3,
                tooltipY + tooltipHeight + 3 - 1,
                borderColorStart,
                borderColorEnd
            )
            drawGradientRect(
                zLevel,
                tooltipX - 3,
                tooltipY - 3,
                tooltipX + tooltipTextWidth + 3,
                tooltipY - 3 + 1,
                borderColorStart,
                borderColorStart
            )
            drawGradientRect(
                zLevel,
                tooltipX - 3,
                tooltipY + tooltipHeight + 2,
                tooltipX + tooltipTextWidth + 3,
                tooltipY + tooltipHeight + 3,
                borderColorEnd,
                borderColorEnd
            )
            for (lineNumber in textLines.indices) {
                val line = textLines[lineNumber]
                font.drawStringWithShadow(line, tooltipX.toFloat(), tooltipY.toFloat(), -1)
                if (lineNumber + 1 == titleLinesCount) {
                    tooltipY += 2
                }
                tooltipY += 10
            }
            GlStateManager.enableLighting()
            GlStateManager.enableDepth()
            RenderHelper.enableStandardItemLighting()
            GlStateManager.enableRescaleNormal()
        }
        GlStateManager.disableLighting()
    }

    /**
     * Adapted from NotEnoughUpdates under LGPL license https://github.com/NotEnoughUpdates/NotEnoughUpdates/blob/master/COPYING.LESSER
     */
    fun drawGradientRect(
        zLevel: Int,
        left: Int,
        top: Int,
        right: Int,
        bottom: Int,
        startColor: Int,
        endColor: Int
    ) {
        val startAlpha = (startColor shr 24 and 255).toFloat() / 255.0f
        val startRed = (startColor shr 16 and 255).toFloat() / 255.0f
        val startGreen = (startColor shr 8 and 255).toFloat() / 255.0f
        val startBlue = (startColor and 255).toFloat() / 255.0f
        val endAlpha = (endColor shr 24 and 255).toFloat() / 255.0f
        val endRed = (endColor shr 16 and 255).toFloat() / 255.0f
        val endGreen = (endColor shr 8 and 255).toFloat() / 255.0f
        val endBlue = (endColor and 255).toFloat() / 255.0f
        GlStateManager.disableTexture2D()
        GlStateManager.enableBlend()
        GlStateManager.disableAlpha()
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)
        GlStateManager.shadeModel(7425)
        val tessellator = Tessellator.getInstance()
        val worldrenderer = tessellator.worldRenderer
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR)
        worldrenderer.pos(right.toDouble(), top.toDouble(), zLevel.toDouble())
            .color(startRed, startGreen, startBlue, startAlpha).endVertex()
        worldrenderer.pos(left.toDouble(), top.toDouble(), zLevel.toDouble())
            .color(startRed, startGreen, startBlue, startAlpha).endVertex()
        worldrenderer.pos(left.toDouble(), bottom.toDouble(), zLevel.toDouble())
            .color(endRed, endGreen, endBlue, endAlpha).endVertex()
        worldrenderer.pos(right.toDouble(), bottom.toDouble(), zLevel.toDouble())
            .color(endRed, endGreen, endBlue, endAlpha).endVertex()
        tessellator.draw()
        GlStateManager.shadeModel(7424)
        GlStateManager.disableBlend()
        GlStateManager.enableAlpha()
        GlStateManager.enableTexture2D()
    }

    fun drawRectBorder(x: Number, y: Number, width: Number, height: Number, color: Int, borderColor: Int) {
        Gui.drawRect(x.toInt(), y.toInt(), x.toInt() + width.toInt(), y.toInt() + height.toInt(), color)
        Gui.drawRect(x.toInt(), y.toInt(), x.toInt() + width.toInt(), y.toInt() + 1, borderColor)
        Gui.drawRect(
            x.toInt(),
            y.toInt() + height.toInt() - 1,
            x.toInt() + width.toInt(),
            y.toInt() + height.toInt(),
            borderColor
        )
        Gui.drawRect(x.toInt(), y.toInt(), x.toInt() + 1, y.toInt() + height.toInt(), borderColor)
        Gui.drawRect(
            x.toInt() + width.toInt() - 1,
            y.toInt(),
            x.toInt() + width.toInt(),
            y.toInt() + height.toInt(),
            borderColor
        )
    }
}
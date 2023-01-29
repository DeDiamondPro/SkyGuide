package dev.dediamondpro.skyguide.gui

import cc.polyfrost.oneconfig.libs.universal.UGraphics
import cc.polyfrost.oneconfig.libs.universal.UMatrixStack
import cc.polyfrost.oneconfig.libs.universal.UResolution
import cc.polyfrost.oneconfig.libs.universal.UScreen
import cc.polyfrost.oneconfig.libs.universal.wrappers.UPlayer
import dev.dediamondpro.skyguide.config.Config
import dev.dediamondpro.skyguide.handlers.AssetHandler
import dev.dediamondpro.skyguide.map.Island
import dev.dediamondpro.skyguide.map.SkyblockMap
import dev.dediamondpro.skyguide.map.navigation.Destination
import dev.dediamondpro.skyguide.map.navigation.NavigationHandler
import dev.dediamondpro.skyguide.utils.*
import org.lwjgl.input.Mouse
import org.lwjgl.opengl.GL11
import java.awt.Color

class MapGui : UScreen() {
    private var world = SkyblockMap.getCurrentWorld() ?: SkyblockMap.worlds.values.firstOrNull()
    private val buttons = mutableListOf<Button>()
    private var buttonsWidth = 0f
    private var scale = Config.defaultScale
    private var x: Float = 0f
    private var y: Float = 0f

    init {
        x = (-(UPlayer.getPosX() + Island.getXOffset()) + (UResolution.scaledWidth / 2f) / scale).toFloat()
        y = (-(UPlayer.getPosZ() + Island.getYOffset()) + (UResolution.scaledHeight / 2f) / scale).toFloat()
        for (worldName in SkyblockMap.worlds.keys) {
            val newButton = Button(worldName, 0f, 0f, 25f) {
                world = SkyblockMap.worlds[it.text]
                scale = Config.defaultScale
                when (world) {
                    null -> displayScreen(null)
                    SkyblockMap.getCurrentWorld() -> {
                        x =
                            (-(UPlayer.getPosX() + Island.getXOffset()) + (UResolution.scaledWidth / 2f) / scale).toFloat()
                        y =
                            (-(UPlayer.getPosZ() + Island.getYOffset()) + (UResolution.scaledHeight / 2f) / scale).toFloat()
                    }

                    else -> {
                        var bottomX = Float.MIN_VALUE
                        var topX = Float.MAX_VALUE
                        var bottomY = Float.MIN_VALUE
                        var topY = Float.MAX_VALUE
                        for (island in world!!.values) {
                            bottomX = bottomX.coerceAtLeast(island.bottomX)
                            topX = topX.coerceAtMost(island.topX)
                            bottomY = bottomY.coerceAtLeast(island.bottomY)
                            topY = topY.coerceAtMost(island.topY)
                        }
                        x = -(bottomX + topX) / 2f + (UResolution.scaledWidth / 2f) / scale
                        y = -(bottomY + topY) / 2f + (UResolution.scaledHeight / 2f) / scale
                    }
                }
            }
            buttonsWidth += newButton.width
            buttons.add(newButton)
        }
    }

    override fun initScreen(width: Int, height: Int) {
        var x = width / 2 - buttonsWidth / 2
        for (button in buttons) {
            button.x = x
            button.y = height - 25f
            x += button.width
        }
    }

    override fun onDrawScreen(matrixStack: UMatrixStack, mouseX: Int, mouseY: Int, partialTicks: Float) {
        if (world == null) return
        val scrollWheel = Mouse.getDWheel()
        if (scrollWheel != 0) {
            val oldScale = scale
            if (scrollWheel > 0) scale *= 1.5f
            else scale /= 1.5f
            x += (mouseX / scale) - (mouseX / oldScale)
            y += (mouseY / scale) - (mouseY / oldScale)
        }

        UGraphics.GL.pushMatrix()
        UGraphics.color4f(1f, 1f, 1f, 1f)
        UGraphics.GL.scale(scale.toDouble(), scale.toDouble(), 1.0)
        if (Mouse.isButtonDown(0) && mouseY <= UResolution.scaledHeight - 25) {
            x += (GuiUtils.mouseDX / scale / UResolution.scaleFactor).toFloat()
            y -= (GuiUtils.mouseDY / scale / UResolution.scaleFactor).toFloat()
        }
        UGraphics.GL.translate(x.toDouble(), y.toDouble(), 0.0)

        for (mapPart in world!!.values) {
            if (mapPart.zone == SBInfo.zone) mapPart.draw(
                UPlayer.getPosX().toFloat(),
                UPlayer.getPosY().toFloat(),
                UPlayer.getPosZ().toFloat()
            ) else mapPart.draw(null, null, null)
        }
        UGraphics.GL.translate(
            (UPlayer.getOffsetX(partialTicks) + Island.getXOffset()).toDouble(),
            (UPlayer.getOffsetY(partialTicks) + Island.getYOffset()).toDouble(),
            0.0
        )
        UGraphics.GL.rotate(
            180f + UPlayer.getHeadRotation(partialTicks),
            0.0f,
            0.0f,
            1.0f
        )
        if (world == SkyblockMap.getCurrentWorld()) RenderUtils.drawImage(
            "/assets/skyguide/player.png",
            -Config.mapPointerSize / 2f,
            -Config.mapPointerSize / 2f,
            Config.mapPointerSize,
            Config.mapPointerSize
        )
        UGraphics.GL.popMatrix()
        val locations = mutableListOf<Pair<Float, Float>>()
        UGraphics.GL.pushMatrix()
        GL11.glEnable(GL11.GL_SCISSOR_TEST)
        GL11.glScissor(
            0,
            (25 * UResolution.scaleFactor).toInt(),
            UResolution.windowWidth,
            UResolution.windowHeight - (25 * UResolution.scaleFactor).toInt()
        )
        for (mapPart in world!!.values) mapPart.drawLast(x, y, scale, locations)
        GL11.glDisable(GL11.GL_SCISSOR_TEST)
        UGraphics.GL.popMatrix()
        RenderUtils.drawRect(0, UResolution.scaledHeight - 25, UResolution.scaledWidth, 25, Color(0, 0, 0, 180).rgb)
        for (button in buttons) {
            button.draw(matrixStack, mouseX, mouseY)
        }
        if (mouseY >= UResolution.scaledHeight - 25) return
        var hovering = false
        for (mapPart in world!!.values) if (mapPart.drawTooltips(
                x,
                y,
                mouseX,
                mouseY,
                scale,
                locations
            )
        ) {
            hovering = true
            break
        }
        if (!hovering && GuiUtils.rightClicked) {
            val xScaled = mouseX / scale - x
            val yScaled = mouseY / scale - y
            for (island in world!!.values) {
                if (!island.isInIsland(xScaled, yScaled)) continue
                NavigationHandler.navigateTo(
                    Destination(
                        island,
                        xScaled - island.xOffset,
                        null,
                        yScaled - island.yOffset
                    )
                )
            }
        }
    }

    override fun onScreenClose() {
        if (!Config.keepAssetsLoaded) AssetHandler.unloadAssets()
    }
}
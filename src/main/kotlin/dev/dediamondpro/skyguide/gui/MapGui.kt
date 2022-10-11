package dev.dediamondpro.skyguide.gui

import dev.dediamondpro.skyguide.config.Config
import dev.dediamondpro.skyguide.handlers.AssetHandler
import dev.dediamondpro.skyguide.map.Island
import dev.dediamondpro.skyguide.map.SkyblockMap
import dev.dediamondpro.skyguide.utils.*
import gg.essential.universal.UGraphics
import gg.essential.universal.UMatrixStack
import gg.essential.universal.UMinecraft
import gg.essential.universal.UResolution
import gg.essential.universal.UScreen
import gg.essential.universal.wrappers.UPlayer
import org.lwjgl.input.Mouse

class MapGui : UScreen() {
    private var scale = Config.defaultScale
    private var x: Float = 0f
    private var y: Float = 0f
    private var topX: Float = Float.MAX_VALUE
    private var topY: Float = Float.MAX_VALUE

    init {
        x = (-(UPlayer.getPosX() + Island.getXOffset()) + (UResolution.scaledWidth / 2f) / scale).toFloat()
        y = (-(UPlayer.getPosZ() + Island.getYOffset()) + (UResolution.scaledHeight / 2f) / scale).toFloat()
        if (!SkyblockMap.currentWorldAvailable()) {
            displayScreen(null)
        } else {
            for (island in SkyblockMap.getCurrentWorld()?.values!!) {
                topX = topX.coerceAtMost(island.topX)
                topY = topY.coerceAtMost(island.topY)
            }
        }
    }

    override fun onDrawScreen(matrixStack: UMatrixStack, mouseX: Int, mouseY: Int, partialTicks: Float) {
        if (!SkyblockMap.currentWorldAvailable()) return
        val scrollWheel = Mouse.getDWheel()
        if (scrollWheel != 0) {
            val oldScale = scale
            if (scrollWheel > 0) scale *= 1.5f
            else scale /= 1.5f
            x += (mouseX / scale) - (mouseX / oldScale)
            y += (mouseY / scale) - (mouseY / oldScale)
        }

        UGraphics.GL.pushMatrix()
        UGraphics.GL.scale(scale.toDouble(), scale.toDouble(), 0.0)
        if (Mouse.isButtonDown(0)) {
            x += (Mouse.getDX() / scale / UResolution.scaleFactor).toFloat()
            y -= (Mouse.getDY() / scale / UResolution.scaleFactor).toFloat()
        }
        UGraphics.GL.translate(x.toDouble(), y.toDouble(), 0.0)

        for (mapPart in SkyblockMap.getCurrentWorld()?.values!!) mapPart.draw(
            if (mapPart.zone == SBInfo.zone) UPlayer.getPosY().toInt()
            else 255,
            scale
        )
        UGraphics.GL.popMatrix()
        UGraphics.GL.pushMatrix()
        UGraphics.GL.translate(x.toDouble(), y.toDouble(), 0.0)
        for (mapPart in SkyblockMap.getCurrentWorld()?.values!!) mapPart.drawLast(
            mouseX - x,
            mouseY - y
        )
        UGraphics.GL.popMatrix()
        UGraphics.GL.pushMatrix()
        UGraphics.GL.translate(
            (UPlayer.getOffsetX() + Island.getXOffset()).toDouble(),
            (UPlayer.getOffsetY() + Island.getYOffset()).toDouble(),
            0.0
        )
        UGraphics.GL.rotate(
            180f + UMinecraft.getMinecraft().thePlayer.rotationYawHead,
            0.0f,
            0.0f,
            1.0f
        )
        RenderUtils.drawImage(
            "/assets/skyguide/player.png",
            -Config.mapPointerSize / 2f,
            -Config.mapPointerSize / 2f,
            Config.mapPointerSize,
            Config.mapPointerSize
        )
        UGraphics.GL.popMatrix()
    }

    override fun onScreenClose() {
        if (!Config.keepAssetsLoaded) AssetHandler.unloadAssets()
    }
}
package dev.dediamondpro.polyblock.gui

import dev.dediamondpro.polyblock.config.BlockConfig
import dev.dediamondpro.polyblock.handlers.AssetHandler
import dev.dediamondpro.polyblock.map.Island
import dev.dediamondpro.polyblock.map.SkyblockMap
import dev.dediamondpro.polyblock.utils.*
import gg.essential.universal.UGraphics
import gg.essential.universal.UMatrixStack
import gg.essential.universal.UMinecraft
import gg.essential.universal.UResolution
import gg.essential.universal.UScreen
import gg.essential.universal.wrappers.UPlayer
import org.lwjgl.input.Mouse
import kotlin.math.pow
import kotlin.math.sqrt

class MapGui : UScreen() {
    private var scale = BlockConfig.defaultScale
    private var x: Float = 0f
    private var y: Float = 0f
    private var topX: Float = Float.MAX_VALUE
    private var topY: Float = Float.MAX_VALUE
    private var wasRightMouseDown = false

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

        val rightMouseDown = Mouse.isButtonDown(1)
        if (rightMouseDown && !wasRightMouseDown) {
            val xCoordinate = (mouseX / scale / UResolution.scaleFactor - x).toFloat()
            val yCoordinate = (mouseX / scale / UResolution.scaleFactor - y).toFloat()
            var done = false
            BlockConfig.waypoints.removeIf {
                val dist = sqrt((xCoordinate - it.x).pow(2) + (yCoordinate - it.y).pow(2))
                if (dist <= 3) done = true
                dist <= 3
            }
            if (!done) {
                for (zone in SkyblockMap.getCurrentWorld()!!.keys.reversed()) {
                    val island = SkyblockMap.getCurrentWorld()!![zone]!!
                    if (!island.isInIsland(xCoordinate, yCoordinate)) continue
                    BlockConfig.waypoints.add(Waypoint(zone, xCoordinate, yCoordinate))
                    break
                }
            }
        }
        UGraphics.GL.scale(scale.toDouble(), scale.toDouble(), 0.0)
        if (Mouse.isButtonDown(0)) {
            x += (Mouse.getDX() / scale / UResolution.scaleFactor).toFloat()
            y -= (Mouse.getDY() / scale / UResolution.scaleFactor).toFloat()
        }
        UGraphics.GL.translate(x.toDouble(), y.toDouble(), 0.0)

        for (mapPart in SkyblockMap.getCurrentWorld()?.values!!) {
            if (mapPart.zone == SBInfo.zone) mapPart.draw(UPlayer.getPosY().toInt())
            else mapPart.draw(255)
        }
        UGraphics.GL.translate(
            (UPlayer.getOffsetX() + Island.getXOffset()).toDouble(),
            (UPlayer.getOffsetY() + Island.getYOffset()).toDouble(),
            0.0
        )
        UGraphics.GL.rotate(
            Math.toRadians(180.0 + UMinecraft.getMinecraft().thePlayer.rotationYawHead).toFloat(),
            0.0f,
            0.0f,
            0.0f
        )
        RenderUtils.drawImage(
            "/assets/polyblock/player.png",
            -BlockConfig.pointerSize / 2f,
            -BlockConfig.pointerSize / 2f,
            BlockConfig.pointerSize,
            BlockConfig.pointerSize
        )
        wasRightMouseDown = rightMouseDown
    }

    override fun onScreenClose() {
        if (!BlockConfig.keepAssetsLoaded) AssetHandler.unloadAssets()
    }
}
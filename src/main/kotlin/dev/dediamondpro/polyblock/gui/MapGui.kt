package dev.dediamondpro.polyblock.gui

import cc.polyfrost.oneconfig.libs.universal.UMinecraft
import cc.polyfrost.oneconfig.libs.universal.UResolution
import cc.polyfrost.oneconfig.libs.universal.wrappers.UPlayer
import cc.polyfrost.oneconfig.platform.Platform
import cc.polyfrost.oneconfig.renderer.RenderManager
import cc.polyfrost.oneconfig.utils.InputHandler
import cc.polyfrost.oneconfig.utils.dsl.drawImage
import cc.polyfrost.oneconfig.utils.dsl.nanoVG
import cc.polyfrost.oneconfig.utils.dsl.scale
import cc.polyfrost.oneconfig.utils.gui.OneUIScreen
import dev.dediamondpro.polyblock.config.BlockConfig
import dev.dediamondpro.polyblock.map.SkyblockMap
import dev.dediamondpro.polyblock.map.Island
import dev.dediamondpro.polyblock.utils.*
import org.lwjgl.input.Mouse
import org.lwjgl.nanovg.NanoVG
import kotlin.math.pow
import kotlin.math.sqrt

class MapGui : OneUIScreen() {
    private var scale = BlockConfig.defaultScale
    private var x: Float = 0f
    private var y: Float = 0f
    private var topX: Float = Float.MAX_VALUE
    private var topY: Float = Float.MAX_VALUE
    private var wasRightMouseDown = false

    init {
        x = (-(UPlayer.getPosX() + Island.getXOffset()) + (UResolution.windowWidth / 2f) / scale).toFloat()
        y = (-(UPlayer.getPosZ() + Island.getYOffset()) + (UResolution.windowHeight / 2f) / scale).toFloat()
        if (!SkyblockMap.currentWorldAvailable()) {
            displayScreen(null)
        } else {
            for (island in SkyblockMap.getCurrentWorld()?.values!!) {
                topX = topX.coerceAtMost(island.topX)
                topY = topY.coerceAtMost(island.topY)
            }
        }
    }

    override fun draw(vg: Long, partialTicks: Float, inputHandler: InputHandler) {
        if (!SkyblockMap.currentWorldAvailable()) return
        val scrollWheel = Platform.getMousePlatform().dWheel
        if (scrollWheel != 0.0) {
            val oldScale = scale
            if (scrollWheel > 0) scale *= 1.5f
            else scale /= 1.5f
            inputHandler.resetScale()
            x += (inputHandler.mouseX() / scale) - (inputHandler.mouseX() / oldScale)
            y += (inputHandler.mouseY() / scale) - (inputHandler.mouseY() / oldScale)
        }
        inputHandler.scale(scale.toDouble(), scale.toDouble())

        val rightMouseDown = Platform.getMousePlatform().isButtonDown(1)
        if (rightMouseDown && !wasRightMouseDown) {
            val xCoordinate = inputHandler.mouseX() - x
            val yCoordinate = inputHandler.mouseY() - y
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
                    BlockConfig.save()
                    break
                }
            } else BlockConfig.save()
        }
        nanoVG(vg) {
            scale(scale, scale)
            if (Mouse.isButtonDown(0)) {
                x += Mouse.getDX() / scale
                y -= Mouse.getDY() / scale
            }
            NanoVG.nvgTranslate(vg, x, y)

            for (mapPart in SkyblockMap.getCurrentWorld()?.values!!) {
                if (mapPart.zone == SBInfo.zone) mapPart.draw(vg, UPlayer.getPosY().toInt())
                else mapPart.draw(vg, 255)
            }
            NanoVG.nvgTranslate(
                vg,
                UPlayer.getOffsetX() + Island.getXOffset(),
                UPlayer.getOffsetY() + Island.getYOffset()
            )
            NanoVG.nvgRotate(
                vg,
                Math.toRadians(180.0 + UMinecraft.getMinecraft().thePlayer.rotationYawHead).toFloat()
            )
            AssetHandler.loadAsset(vg, "/assets/polyblock/player.png")
            drawImage(
                "/assets/polyblock/player.png",
                -BlockConfig.pointerSize / 2f,
                -BlockConfig.pointerSize / 2f,
                BlockConfig.pointerSize,
                BlockConfig.pointerSize
            )
        }
        wasRightMouseDown = rightMouseDown
    }

    override fun onScreenClose() {
        if (!BlockConfig.keepAssetsLoaded) RenderManager.setupAndDraw { AssetHandler.unloadAssets(it) }
    }

    override fun hasBackgroundBlur(): Boolean {
        return true
    }
}
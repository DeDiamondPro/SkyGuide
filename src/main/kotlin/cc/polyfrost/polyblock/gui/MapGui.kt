package cc.polyfrost.polyblock.gui

import cc.polyfrost.oneconfig.libs.universal.UMinecraft
import cc.polyfrost.oneconfig.libs.universal.UResolution
import cc.polyfrost.oneconfig.libs.universal.UScreen
import cc.polyfrost.oneconfig.libs.universal.wrappers.UPlayer
import cc.polyfrost.oneconfig.platform.Platform
import cc.polyfrost.oneconfig.renderer.RenderManager
import cc.polyfrost.oneconfig.utils.InputHandler
import cc.polyfrost.oneconfig.utils.dsl.drawImage
import cc.polyfrost.oneconfig.utils.dsl.nanoVG
import cc.polyfrost.oneconfig.utils.dsl.scale
import cc.polyfrost.oneconfig.utils.gui.OneUIScreen
import cc.polyfrost.polyblock.PolyBlock
import cc.polyfrost.polyblock.config.BlockConfig
import cc.polyfrost.polyblock.map.SkyblockMap
import cc.polyfrost.polyblock.utils.AssetHandler
import cc.polyfrost.polyblock.map.Island
import cc.polyfrost.polyblock.utils.getX
import cc.polyfrost.polyblock.utils.getY
import org.lwjgl.input.Mouse
import org.lwjgl.nanovg.NanoVG

class MapGui : OneUIScreen() {
    private var scale = BlockConfig.defaultScale
    private var x: Float = 0f
    private var y: Float = 0f

    init {
        x = (-(UPlayer.getPosX() + Island.getXOffset()) + (UResolution.windowWidth / 2f) / scale).toFloat()
        y = (-(UPlayer.getPosZ() + Island.getYOffset()) + (UResolution.windowHeight / 2f) / scale).toFloat()
    }

    override fun draw(vg: Long, partialTicks: Float, inputHandler: InputHandler) {
        if (!SkyblockMap.currentWorldAvailable()) {
            displayScreen(null)
            return
        }
        nanoVG(vg) {
            val scrollWheel = Platform.getMousePlatform().dWheel

            if (scrollWheel != 0.0) {
                val oldScale = scale
                if (scrollWheel > 0) scale *= 1.5f
                else scale /= 1.5f
                inputHandler.resetScale()
                x += (inputHandler.mouseX() / scale) - (inputHandler.mouseX() / oldScale)
                y += (inputHandler.mouseY() / scale) - (inputHandler.mouseY() / oldScale)
            }
            scale(scale, scale)
            inputHandler.scale(scale.toDouble(), scale.toDouble())
            if (Mouse.isButtonDown(0)) {
                x += Mouse.getDX() / scale
                y -= Mouse.getDY() / scale
            }
            NanoVG.nvgTranslate(vg, x, y)

            for (mapPart in SkyblockMap.getCurrentWorld()?.values!!) {
                mapPart.draw(vg)
            }
            NanoVG.nvgTranslate(vg, UPlayer.getX() + Island.getXOffset(), UPlayer.getY() + Island.getYOffset())
            NanoVG.nvgRotate(vg, Math.toRadians(180.0 + UMinecraft.getMinecraft().thePlayer.rotationYawHead).toFloat())
            AssetHandler.loadAsset(vg, "/assets/polyblock/player.png")
            drawImage(
                "/assets/polyblock/player.png",
                -BlockConfig.pointerSize / 2f,
                -BlockConfig.pointerSize / 2f,
                BlockConfig.pointerSize,
                BlockConfig.pointerSize
            )
        }
    }

    override fun onScreenClose() {
        if (!BlockConfig.keepAssetsLoaded) RenderManager.setupAndDraw { AssetHandler.unloadAssets(it) }
    }

    override fun hasBackgroundBlur(): Boolean {
        return true
    }
}
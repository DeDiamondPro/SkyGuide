package dev.dediamondpro.polyblock.hud

import cc.polyfrost.oneconfig.config.annotations.Exclude
import cc.polyfrost.oneconfig.config.annotations.Slider
import cc.polyfrost.oneconfig.config.annotations.Switch
import cc.polyfrost.oneconfig.gui.animations.Animation
import cc.polyfrost.oneconfig.gui.animations.DummyAnimation
import cc.polyfrost.oneconfig.gui.animations.EaseInOutQuad
import cc.polyfrost.oneconfig.hud.Hud
import cc.polyfrost.oneconfig.libs.universal.UMatrixStack
import cc.polyfrost.oneconfig.libs.universal.UMinecraft
import cc.polyfrost.oneconfig.libs.universal.UResolution
import cc.polyfrost.oneconfig.libs.universal.wrappers.UPlayer
import cc.polyfrost.oneconfig.platform.Platform
import cc.polyfrost.oneconfig.renderer.scissor.ScissorManager
import cc.polyfrost.oneconfig.utils.dsl.*
import dev.dediamondpro.polyblock.gui.MapGui
import dev.dediamondpro.polyblock.map.Island
import dev.dediamondpro.polyblock.map.SkyblockMap
import dev.dediamondpro.polyblock.map.Textures
import dev.dediamondpro.polyblock.utils.AssetHandler
import dev.dediamondpro.polyblock.utils.SBInfo
import dev.dediamondpro.polyblock.utils.getOffsetX
import dev.dediamondpro.polyblock.utils.getOffsetY
import org.lwjgl.nanovg.NanoVG

class MiniMap : Hud(true) {

    @Switch(
        name = "Rotate With Player",
        description = "Rotate the map with the player."
    )
    var rotateWithPlayer = true

    @Slider(
        name = "Zoom Factor",
        description = "The zoom factor of the map.",
        min = 0.25f, max = 5f
    )
    var mapZoom = 1f

    @Slider(
        name = "Player Pointer Size",
        description = "The size of the player pointer.",
        min = 3.5f, max = 35f
    )
    var pointerSize = 7f

    @Exclude
    var zoomAnimation: Animation = DummyAnimation(1f)

    @Exclude
    var fadeAnimation: Animation = DummyAnimation(1f)

    @Exclude
    var prevIsland: Island? = null

    @Exclude
    var prevImage: Textures? = null

    override fun draw(matrices: UMatrixStack?, xUnscaled: Float, yUnscaled: Float, s: Float, example: Boolean) {
        val island = SkyblockMap.getCurrentIsland() ?: return
        val scale = s * UResolution.scaleFactor.toFloat()
        val x = xUnscaled * UResolution.scaleFactor.toFloat()
        val y = yUnscaled * UResolution.scaleFactor.toFloat()
        val image = island.getImage(UPlayer.getPosY().toInt())
        if (image.zoom != zoomAnimation.end) {
            zoomAnimation = if (prevIsland != island) DummyAnimation(image.zoom)
            else EaseInOutQuad(350, zoomAnimation.get(), image.zoom, false)
        }
        val totalScale = scale * mapZoom * zoomAnimation.get()
        if (island == prevIsland && prevImage != image && fadeAnimation.isFinished)
            fadeAnimation = EaseInOutQuad(350, 0f, 1f, false)
        nanoVG {
            val vg = this.instance
            val scissor = ScissorManager.scissor(vg, x, y, 150f * scale, 150f * scale)
            translate(x + 75f * scale, y + 75f * scale)
            if (rotateWithPlayer) {
                NanoVG.nvgRotate(
                    vg,
                    Math.toRadians(180.0 - UMinecraft.getMinecraft().thePlayer.rotationYawHead).toFloat()
                )
            }
            if (!fadeAnimation.isFinished && prevImage != null) {
                prevImage!!.draw(
                    vg,
                    ((island.topX - UPlayer.getOffsetX()) * totalScale).toInt(),
                    ((island.topY - UPlayer.getOffsetY()) * totalScale).toInt(),
                    island.width * totalScale,
                    island.height * totalScale
                )
            }
            setAlpha(fadeAnimation.get())
            image.draw(
                vg,
                ((island.topX - UPlayer.getOffsetX()) * totalScale).toInt(),
                ((island.topY - UPlayer.getOffsetY()) * totalScale).toInt(),
                island.width * totalScale,
                island.height * totalScale
            )
            setAlpha(1f)
            resetTransform()
            translate(x + 75f * scale, y + 75f * scale)
            if (!rotateWithPlayer) {
                NanoVG.nvgRotate(
                    vg,
                    Math.toRadians(180.0 + UMinecraft.getMinecraft().thePlayer.rotationYawHead).toFloat()
                )
            }
            AssetHandler.loadAsset(vg, "/assets/polyblock/player.png")
            drawImage(
                "/assets/polyblock/player.png",
                -pointerSize * scale / 2,
                -pointerSize * scale / 2,
                pointerSize * scale,
                pointerSize * scale
            )
            ScissorManager.resetScissor(vg, scissor)
        }
        if (fadeAnimation.isFinished) {
            if (island != prevIsland) prevIsland = island
            if (image != prevImage) prevImage = image
        }
    }

    override fun getWidth(scale: Float, example: Boolean): Float {
        return 150f * scale
    }

    override fun getHeight(scale: Float, example: Boolean): Float {
        return 150f * scale
    }

    override fun shouldShow(): Boolean {
        return super.shouldShow()
                && SBInfo.inSkyblock
                && Platform.getGuiPlatform().currentScreen !is MapGui
                && SkyblockMap.currentIslandAvailable()
    }
}
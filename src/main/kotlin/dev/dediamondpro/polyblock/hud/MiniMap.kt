package dev.dediamondpro.polyblock.hud

import cc.polyfrost.oneconfig.config.annotations.Slider
import cc.polyfrost.oneconfig.config.annotations.Switch
import cc.polyfrost.oneconfig.hud.Hud
import cc.polyfrost.oneconfig.libs.universal.UMatrixStack
import cc.polyfrost.oneconfig.libs.universal.UMinecraft
import cc.polyfrost.oneconfig.libs.universal.UResolution
import cc.polyfrost.oneconfig.libs.universal.wrappers.UPlayer
import cc.polyfrost.oneconfig.platform.Platform
import cc.polyfrost.oneconfig.renderer.scissor.ScissorManager
import cc.polyfrost.oneconfig.utils.dsl.drawImage
import cc.polyfrost.oneconfig.utils.dsl.nanoVG
import dev.dediamondpro.polyblock.gui.MapGui
import dev.dediamondpro.polyblock.map.SkyblockMap
import dev.dediamondpro.polyblock.utils.AssetHandler
import dev.dediamondpro.polyblock.utils.SBInfo
import dev.dediamondpro.polyblock.utils.getOffsetX
import dev.dediamondpro.polyblock.utils.getOffsetY
import org.lwjgl.nanovg.NanoVG

class MiniMap : Hud(true) {

    @Switch(name = "Rotate With Player")
    var rotateWithPlayer = true

    @Slider(
        name = "Zoom Factor",
        min = 0.25f, max = 5f
    )
    var mapZoom = 1f

    @Slider(
        name = "Player Pointer Size",
        min = 3.5f, max = 35f
    )
    var pointerSize = 7f

    override fun draw(matrices: UMatrixStack?, xUnscaled: Float, yUnscaled: Float, s: Float, example: Boolean) {
        val island = SkyblockMap.getCurrentIsland() ?: return
        val scale = s * UResolution.scaleFactor.toFloat()
        val x = xUnscaled * UResolution.scaleFactor.toFloat()
        val y = yUnscaled * UResolution.scaleFactor.toFloat()
        val totalScale = scale * mapZoom
        nanoVG {
            val vg = this.instance
            val scissor = ScissorManager.scissor(vg, x, y, 150f * scale, 150f * scale)
            NanoVG.nvgTranslate(vg, x + 75f * scale, y + 75f * scale)
            if (rotateWithPlayer) {
                NanoVG.nvgRotate(
                    vg,
                    Math.toRadians(180.0 - UMinecraft.getMinecraft().thePlayer.rotationYawHead).toFloat()
                )
            }
            island.image.draw(
                vg,
                ((island.topX - UPlayer.getOffsetX()) * totalScale).toInt(),
                ((island.topY - UPlayer.getOffsetY()) * totalScale).toInt(),
                island.width * totalScale,
                island.height * totalScale
            )
            NanoVG.nvgResetTransform(vg)
            NanoVG.nvgTranslate(vg, x + 75f * scale, y + 75f * scale)
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
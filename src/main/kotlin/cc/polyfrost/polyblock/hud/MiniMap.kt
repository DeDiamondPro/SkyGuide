package cc.polyfrost.polyblock.hud

import cc.polyfrost.oneconfig.config.annotations.Slider
import cc.polyfrost.oneconfig.hud.Hud
import cc.polyfrost.oneconfig.libs.universal.UMatrixStack
import cc.polyfrost.oneconfig.libs.universal.UMinecraft
import cc.polyfrost.oneconfig.libs.universal.wrappers.UPlayer
import cc.polyfrost.oneconfig.platform.Platform
import cc.polyfrost.oneconfig.renderer.RenderManager
import cc.polyfrost.oneconfig.renderer.scissor.ScissorManager
import cc.polyfrost.polyblock.gui.MapGui
import cc.polyfrost.polyblock.map.SkyblockMap
import cc.polyfrost.polyblock.utils.SBInfo
import org.lwjgl.nanovg.NanoVG

class MiniMap : Hud() {

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

    override fun draw(matrices: UMatrixStack?, x: Float, y: Float, scale: Float, example: Boolean) {
        val island = SkyblockMap.mapParts[SBInfo.zone] ?: return
        val totalScale = scale * mapZoom
        RenderManager.setupAndDraw(true) { vg ->
            val scissor = ScissorManager.scissor(vg, x, y, 150f * scale, 150f * scale)
            RenderManager.drawImage(
                vg,
                island.image,
                (x + (island.topX - UPlayer.getPosX()) * totalScale + 75f * scale).toFloat(),
                (y + (island.topY - UPlayer.getPosZ()) * totalScale + 75f * scale).toFloat(),
                island.width * totalScale,
                island.height * totalScale
            )
            NanoVG.nvgTranslate(vg, x + 75f * scale, y + 75f * scale)
            NanoVG.nvgRotate(vg, Math.toRadians(180.0 + UMinecraft.getMinecraft().thePlayer.rotationYawHead).toFloat())
            RenderManager.drawImage(
                vg,
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
        return super.shouldShow() && SBInfo.inSkyblock && Platform.getGuiPlatform().currentScreen !is MapGui
    }
}
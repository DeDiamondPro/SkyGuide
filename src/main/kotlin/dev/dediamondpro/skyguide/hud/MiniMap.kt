package dev.dediamondpro.skyguide.hud


import dev.dediamondpro.skyguide.config.Config
import dev.dediamondpro.skyguide.map.Island
import dev.dediamondpro.skyguide.map.SkyblockMap
import dev.dediamondpro.skyguide.map.Textures
import dev.dediamondpro.skyguide.utils.GuiUtils
import dev.dediamondpro.skyguide.utils.RenderUtils
import dev.dediamondpro.skyguide.utils.getOffsetX
import dev.dediamondpro.skyguide.utils.getOffsetY
import gg.essential.universal.UGraphics
import gg.essential.universal.UMinecraft
import gg.essential.universal.UResolution
import gg.essential.universal.UScreen
import gg.essential.universal.wrappers.UPlayer
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import org.lwjgl.opengl.GL11
import kotlin.math.pow

class MiniMap {
    private var fadeProgress = 1f

    private var zoomStart = 1f
    private var zoomChange = 0f
    private var zoomProgress = 1f

    private var prevIsland: Island? = null
    private var prevImage: Textures? = null

    @SubscribeEvent
    fun draw(event: RenderGameOverlayEvent) {
        if (event.type != RenderGameOverlayEvent.ElementType.ALL || !shouldShow()) return
        val island = SkyblockMap.getCurrentIsland() ?: return
        val scale = Config.miniMapScale
        val x = if (Config.miniMapLocation % 2 == 0) 0f else UResolution.scaledWidth - 150f * scale
        val y = if (Config.miniMapLocation < 2) 0f else UResolution.scaledHeight - 150f * scale
        val image = island.getImage(UPlayer.getPosY().toInt())
        if (prevIsland == island && image.zoom != zoomStart + zoomChange) {
            zoomStart += zoomChange * easeInOutQuad(zoomProgress)
            zoomChange = image.zoom - zoomStart
            zoomProgress = 0f
        } else if (image.zoom != zoomStart + zoomChange) {
            zoomStart = image.zoom
            zoomChange = 0f
            zoomProgress = 1f
        }
        if (zoomProgress != 1f) zoomProgress += GuiUtils.getDeltaTime() / 350f
        zoomProgress = zoomProgress.coerceAtMost(1f)
        val totalScale = scale * Config.mapZoom * (zoomStart + zoomChange * easeInOutQuad(zoomProgress))
        if (island == prevIsland && prevImage != image && fadeProgress == 1f) fadeProgress = 0f
        if (fadeProgress != 1f) fadeProgress += GuiUtils.getDeltaTime() / 350f
        fadeProgress = fadeProgress.coerceAtMost(1f)

        UGraphics.GL.pushMatrix()
        GL11.glEnable(GL11.GL_SCISSOR_TEST)
        UGraphics.enableBlend()
        GL11.glScissor(
            (x * UResolution.scaleFactor).toInt(),
            ((UResolution.scaledHeight - y - 150 * scale) * UResolution.scaleFactor).toInt(),
            (150f * scale * UResolution.scaleFactor).toInt(),
            (150f * scale * UResolution.scaleFactor).toInt()
        )
        UGraphics.GL.translate(x + 75.0 * scale, y + 75.0 * scale, 0.0)
        if (Config.rotateWithPlayer) {
            UGraphics.GL.rotate(
                180f - UMinecraft.getMinecraft().thePlayer.rotationYawHead,
                0.0f,
                0.0f,
                1.0f
            )
        }
        if (fadeProgress != 1f && prevImage != null) {
            prevImage!!.draw(
                ((island.topX - UPlayer.getOffsetX()) * totalScale).toInt(),
                ((island.topY - UPlayer.getOffsetY()) * totalScale).toInt(),
                island.width * totalScale,
                island.height * totalScale
            )
        }
        UGraphics.color4f(1f, 1f, 1f, easeInOutQuad(fadeProgress))
        image.draw(
            (island.topX - UPlayer.getOffsetX()) * totalScale,
            (island.topY - UPlayer.getOffsetY()) * totalScale,
            island.width * totalScale,
            island.height * totalScale
        )
        UGraphics.color4f(1f, 1f, 1f, 1f)
        GL11.glDisable(GL11.GL_SCISSOR_TEST)
        UGraphics.disableBlend()
        UGraphics.GL.popMatrix()
        UGraphics.GL.pushMatrix()
        UGraphics.GL.translate(x + 75.0 * scale, y + 75.0 * scale, 0.0)
        if (!Config.rotateWithPlayer) {
            UGraphics.GL.rotate(
                180f + UMinecraft.getMinecraft().thePlayer.rotationYawHead,
                0.0f,
                0.0f,
                1.0f
            )
        }
        RenderUtils.drawImage(
            "/assets/skyguide/player.png",
            -Config.miniMapPointerSize * scale / 2,
            -Config.miniMapPointerSize * scale / 2,
            Config.miniMapPointerSize * scale,
            Config.miniMapPointerSize * scale
        )
        UGraphics.GL.popMatrix()
        if (fadeProgress == 1f) {
            if (island != prevIsland) prevIsland = island
            if (image != prevImage) prevImage = image
        }
    }

    private fun shouldShow(): Boolean {
        return Config.miniMapEnabled
                && (Config.showInGUIs || UScreen.currentScreen == null || UMinecraft.getMinecraft().ingameGUI.chatGUI.chatOpen)
                && (Config.showInChat || !UMinecraft.getMinecraft().ingameGUI.chatGUI.chatOpen)
                && (Config.showInF3 || !UMinecraft.getMinecraft().gameSettings.showDebugInfo)
    }

    private fun easeInOutQuad(x: Float): Float {
        return if (x < 0.5) 2 * x * x else (1 - (-2 * x + 2).toDouble().pow(2.0) / 2).toFloat()
    }
}
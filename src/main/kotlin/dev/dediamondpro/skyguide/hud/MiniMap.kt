package dev.dediamondpro.skyguide.hud


import dev.dediamondpro.skyguide.config.Config
import dev.dediamondpro.skyguide.map.Island
import dev.dediamondpro.skyguide.map.SkyblockMap
import dev.dediamondpro.skyguide.map.Textures
import dev.dediamondpro.skyguide.utils.*
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
    fun draw(event: RenderGameOverlayEvent.Post) {
        if (event.type != RenderGameOverlayEvent.ElementType.ALL || !shouldShow()) return
        val island = SkyblockMap.getCurrentIsland() ?: return
        val scale = Config.miniMapScale
        val x = if (Config.miniMapLocation % 2 == 0) 0f else UResolution.scaledWidth - 150f * scale
        val y = if (Config.miniMapLocation < 2) 0f else UResolution.scaledHeight - 150f * scale
        val image =
            island.getImage(UPlayer.getPosX().toFloat(), UPlayer.getPosY().toFloat(), UPlayer.getPosZ().toFloat())
        val zoomTarget = if (image.underground) Config.undergroundMapZoom else 1f
        if (prevIsland == island && zoomTarget != zoomStart + zoomChange) {
            zoomStart += zoomChange * easeInOutQuad(zoomProgress)
            zoomChange = zoomTarget - zoomStart
            zoomProgress = 0f
        } else if (zoomTarget != zoomStart + zoomChange) {
            zoomStart = zoomTarget
            zoomChange = 0f
            zoomProgress = 1f
        }
        if (zoomProgress != 1f) zoomProgress += GuiUtils.getHudDeltaTime() / 350f
        zoomProgress = zoomProgress.coerceAtMost(1f)
        val totalScale = scale * Config.mapZoom * (zoomStart + zoomChange * easeInOutQuad(zoomProgress))
        if (island == prevIsland && prevImage != image && fadeProgress == 1f) fadeProgress = 0f
        if (fadeProgress != 1f) fadeProgress += GuiUtils.getHudDeltaTime() / 350f
        fadeProgress = fadeProgress.coerceAtMost(1f)

        UGraphics.GL.pushMatrix()
        GL11.glEnable(GL11.GL_SCISSOR_TEST)
        GL11.glEnable(GL11.GL_ALPHA_TEST)
        GL11.glScissor(
            (x * UResolution.scaleFactor).toInt(),
            ((UResolution.scaledHeight - y - 150 * scale) * UResolution.scaleFactor).toInt(),
            (150f * scale * UResolution.scaleFactor).toInt(),
            (150f * scale * UResolution.scaleFactor).toInt()
        )
        if (Config.background) RenderUtils.drawRect(x, y, 150 * scale, 150 * scale, Config.backgroundColor.rgb)
        UGraphics.GL.translate(x + 75.0 * scale, y + 75.0 * scale, 0.0)
        if (Config.rotateWithPlayer) {
            UGraphics.GL.rotate(
                180f - UPlayer.getHeadRotation(event.partialTicks),
                0.0f,
                0.0f,
                1.0f
            )
        }
        if (fadeProgress != 1f && prevImage != null) {
            UGraphics.color4f(1f, 1f, 1f, 1f - easeInOutQuad(fadeProgress))
            prevImage!!.draw(
                (island.topX - UPlayer.getOffsetX(event.partialTicks)) * totalScale,
                (island.topY - UPlayer.getOffsetY(event.partialTicks)) * totalScale,
                island.width * totalScale,
                island.height * totalScale
            )
        }
        UGraphics.color4f(1f, 1f, 1f, easeInOutQuad(fadeProgress))
        image.draw(
            (island.topX - UPlayer.getOffsetX(event.partialTicks)) * totalScale,
            (island.topY - UPlayer.getOffsetY(event.partialTicks)) * totalScale,
            island.width * totalScale,
            island.height * totalScale,
            if (Config.smoothImages) GL11.GL_LINEAR else GL11.GL_NEAREST
        )
        UGraphics.color4f(1f, 1f, 1f, 1f)
        UGraphics.GL.popMatrix()
        island.drawPioMiniMap(
            x + 75 * scale - UPlayer.getOffsetX(event.partialTicks) * totalScale,
            y + 75 * scale - UPlayer.getOffsetY(event.partialTicks) * totalScale,
            x + 75.0 * scale,
            y + 75.0 * scale,
            totalScale,
            if (Config.rotateWithPlayer) Math.toRadians(180.0 + UPlayer.getHeadRotation(event.partialTicks)) else 0.0
        )
        UGraphics.GL.pushMatrix()
        GL11.glScissor(
            (x * UResolution.scaleFactor).toInt(),
            ((UResolution.scaledHeight - y - 150 * scale) * UResolution.scaleFactor).toInt(),
            (150f * scale * UResolution.scaleFactor).toInt(),
            (150f * scale * UResolution.scaleFactor).toInt()
        )
        UGraphics.GL.translate(x + 75.0 * scale, y + 75.0 * scale, 0.0)
        UGraphics.disableDepth()
        if (!Config.rotateWithPlayer) {
            UGraphics.GL.rotate(
                180f + UPlayer.getHeadRotation(event.partialTicks),
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
        UGraphics.enableDepth()
        GL11.glDisable(GL11.GL_SCISSOR_TEST)
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
        return if (x < 0.5f) 2f * x * x else 1f - (-2f * x + 2f).pow(2f) / 2f
    }
}
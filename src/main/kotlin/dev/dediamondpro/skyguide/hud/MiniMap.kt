package dev.dediamondpro.skyguide.hud


import cc.polyfrost.oneconfig.config.annotations.Exclude
import cc.polyfrost.oneconfig.config.annotations.Slider
import cc.polyfrost.oneconfig.config.annotations.Switch
import cc.polyfrost.oneconfig.config.core.OneColor
import cc.polyfrost.oneconfig.hud.Hud
import cc.polyfrost.oneconfig.libs.universal.UGraphics
import cc.polyfrost.oneconfig.libs.universal.UMatrixStack
import cc.polyfrost.oneconfig.libs.universal.UResolution
import cc.polyfrost.oneconfig.libs.universal.UScreen
import cc.polyfrost.oneconfig.libs.universal.wrappers.UPlayer
import dev.dediamondpro.skyguide.gui.MapGui
import dev.dediamondpro.skyguide.map.Island
import dev.dediamondpro.skyguide.map.SkyblockMap
import dev.dediamondpro.skyguide.map.Textures
import dev.dediamondpro.skyguide.utils.*
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.fml.common.eventhandler.EventPriority
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import org.lwjgl.opengl.GL11
import kotlin.math.pow

class MiniMap : Hud(true, 0f, 1815f, 0.7f) {
    @Switch(
        name = "Rotate With Player",
        description = "Rotate the map with the player.",
        category = "Mini-Map"
    )
    var rotateWithPlayer = true

    @Switch(
        name = "Show PIOs",
        description = "Whether to show points of interests (npcs, portals, ...) on the mini-map.",
        category = "Mini-Map"
    )
    var showPIOs = true

    @Switch(
        name = "Background",
        description = "Whether the map has a background",
        category = "Mini-Map"
    )
    var background = false

    @cc.polyfrost.oneconfig.config.annotations.Color(
        name = "Background Color",
        description = "The color of the background",
        category = "Mini-Map"
    )
    var backgroundColor = OneColor(0, 0, 0)

    @Slider(
        name = "Zoom Factor",
        description = "The zoom factor of the map.",
        min = 0.25f, max = 5f,
        category = "Mini-Map"
    )
    var mapZoom = 1.5f

    @Slider(
        name = "Underground Zoom Multiplier",
        description = "The zoom multiplier of the map when underground.",
        min = 0.25f, max = 5f,
        category = "Mini-Map"
    )
    var undergroundMapZoom = 2f

    @Slider(
        name = "Player Pointer Size",
        description = "The size of the player pointer.",
        min = 3.5f, max = 35f,
        category = "Mini-Map"
    )
    var miniMapPointerSize = 12f

    @Switch(
        name = "Smooth images",
        description = "Smooth the images using linear scaling. Can reduce flicker of the mini-map on some monitors but might look worse.",
        category = "Mini-Map"
    )
    var smoothImages = false

    @Exclude
    private var fadeProgress = 1f

    @Exclude
    private var zoomStart = 1f

    @Exclude
    private var zoomChange = 0f

    @Exclude
    private var zoomProgress = 1f

    @Exclude
    private var prevIsland: Island? = null

    @Exclude
    private var prevImage: Textures? = null

    @Exclude
    private var partialTicks = 0f

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    fun draw(event: RenderGameOverlayEvent.Pre) {
        if (event.type != RenderGameOverlayEvent.ElementType.ALL || !shouldShow()) return
        partialTicks = event.partialTicks
    }

    override fun draw(matrices: UMatrixStack?, x: Float, y: Float, scale: Float, example: Boolean) {
        val island = SkyblockMap.getCurrentIsland() ?: return
        val image =
            island.getImage(UPlayer.getPosX().toFloat(), UPlayer.getPosY().toFloat(), UPlayer.getPosZ().toFloat())
        val zoomTarget = if (image.underground) undergroundMapZoom else 1f
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
        val totalScale = scale * mapZoom * (zoomStart + zoomChange * easeInOutQuad(zoomProgress))
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
        if (background) RenderUtils.drawRect(x, y, 150 * scale, 150 * scale, backgroundColor.rgb)
        UGraphics.GL.translate(x + 75.0 * scale, y + 75.0 * scale, 0.0)
        if (rotateWithPlayer) {
            UGraphics.GL.rotate(
                180f - UPlayer.getHeadRotation(partialTicks),
                0.0f,
                0.0f,
                1.0f
            )
        }
        if (fadeProgress != 1f && prevImage != null) {
            UGraphics.color4f(1f, 1f, 1f, 1f - easeInOutQuad(fadeProgress))
            prevImage!!.draw(
                (island.topX - UPlayer.getOffsetX(partialTicks)) * totalScale,
                (island.topY - UPlayer.getOffsetY(partialTicks)) * totalScale,
                island.width * totalScale,
                island.height * totalScale
            )
        }
        UGraphics.color4f(1f, 1f, 1f, easeInOutQuad(fadeProgress))
        image.draw(
            (island.topX - UPlayer.getOffsetX(partialTicks)) * totalScale,
            (island.topY - UPlayer.getOffsetY(partialTicks)) * totalScale,
            island.width * totalScale,
            island.height * totalScale,
            if (smoothImages) GL11.GL_LINEAR else GL11.GL_NEAREST
        )
        UGraphics.color4f(1f, 1f, 1f, 1f)
        UGraphics.GL.popMatrix()
        if (showPIOs) island.drawPioMiniMap(
            x + 75 * scale - UPlayer.getOffsetX(partialTicks) * totalScale,
            y + 75 * scale - UPlayer.getOffsetY(partialTicks) * totalScale,
            x + 75.0 * scale,
            y + 75.0 * scale,
            totalScale,
            if (rotateWithPlayer) Math.toRadians(180.0 + UPlayer.getHeadRotation(partialTicks)) else 0.0
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
        if (!rotateWithPlayer) {
            UGraphics.GL.rotate(
                180f + UPlayer.getHeadRotation(partialTicks),
                0.0f,
                0.0f,
                1.0f
            )
        }
        RenderUtils.drawImage(
            "/assets/skyguide/player.png",
            -miniMapPointerSize * scale / 2,
            -miniMapPointerSize * scale / 2,
            miniMapPointerSize * scale,
            miniMapPointerSize * scale
        )
        UGraphics.enableDepth()
        GL11.glDisable(GL11.GL_SCISSOR_TEST)
        UGraphics.GL.popMatrix()
        if (fadeProgress == 1f) {
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

    private fun easeInOutQuad(x: Float): Float {
        return if (x < 0.5f) 2f * x * x else 1f - (-2f * x + 2f).pow(2f) / 2f
    }

    override fun shouldShow(): Boolean {
        return super.shouldShow() && SBInfo.inSkyblock && SkyblockMap.currentIslandAvailable() && UScreen.currentScreen !is MapGui
    }
}
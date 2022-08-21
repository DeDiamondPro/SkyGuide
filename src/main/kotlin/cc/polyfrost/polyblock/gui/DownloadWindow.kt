package cc.polyfrost.polyblock.gui

import cc.polyfrost.oneconfig.events.event.HudRenderEvent
import cc.polyfrost.oneconfig.libs.eventbus.Subscribe
import cc.polyfrost.oneconfig.libs.universal.UResolution
import cc.polyfrost.oneconfig.renderer.font.Fonts
import cc.polyfrost.oneconfig.utils.dsl.drawRoundedRect
import cc.polyfrost.oneconfig.utils.dsl.drawRoundedRectVaried
import cc.polyfrost.oneconfig.utils.dsl.drawText
import cc.polyfrost.oneconfig.utils.dsl.nanoVG
import java.awt.Color

class DownloadWindow {
    private val primary500 = Color(25, 103, 255, 255).rgb
    private val white = Color(255, 255, 255, 255).rgb
    private val white80 = Color(255, 255, 255, 204).rgb
    private val gray900 = Color(13, 14, 15, 255).rgb
    private val gray700 = Color(34, 35, 38, 255).rgb

    companion object {
        var downloading = false
        var totalFiles = 0
        var currentFile = 0
        var currentPercent = 0f

        fun setupDownload(assets: Int) {
            totalFiles = assets
            currentFile = 0
            currentPercent = 0f
            downloading = true
        }
    }

    @Subscribe
    fun onHudRender(event: HudRenderEvent) {
        if (!downloading) return
        val progress = (currentFile.toFloat() + currentPercent) / totalFiles.toFloat();
        nanoVG {
            val x = UResolution.windowWidth - 300f
            val y = UResolution.windowHeight - 80f
            drawRoundedRectVaried(x, y, 300f, 80f, gray900, 12, 0, 0, 0)
            drawText("Downloading assets", x + 12, y + 21, white, 18f, Fonts.SEMIBOLD)
            drawText("PolyBlock by Polyfrost", x + 12, y + 39, white80, 14f, Fonts.REGULAR)
            drawRoundedRect(x + 12.5f, y + 54, 275, 12, 6, gray700)
            drawRoundedRect(x + 12.5f, y + 54, 275 * progress, 12, 6, primary500)
        }
    }
}
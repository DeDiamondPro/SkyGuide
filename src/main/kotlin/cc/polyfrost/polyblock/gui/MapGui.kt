package cc.polyfrost.polyblock.gui

import cc.polyfrost.oneconfig.libs.universal.wrappers.UPlayer
import cc.polyfrost.oneconfig.renderer.RenderManager
import cc.polyfrost.oneconfig.utils.InputHandler
import cc.polyfrost.oneconfig.utils.gui.OneUIScreen
import cc.polyfrost.polyblock.map.SkyblockMap
import java.awt.Color

class MapGui : OneUIScreen() {
    private val topX = SkyblockMap.topX
    private val topY = SkyblockMap.topY
    private val bottomX = SkyblockMap.bottomX
    private val bottomY = SkyblockMap.bottomY

    override fun draw(vg: Long, partialTicks: Float, inputHandler: InputHandler) {
        for (mapPart in SkyblockMap.mapParts) {
            mapPart.draw(vg, topX, topY)
        }
        RenderManager.drawCircle(vg, (UPlayer.getPosX() - topX).toFloat(), (UPlayer.getPosZ() - topY).toFloat(), 5f, Color(255, 0, 0).rgb)
    }
}
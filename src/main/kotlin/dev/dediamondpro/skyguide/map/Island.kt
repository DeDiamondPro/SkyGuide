package dev.dediamondpro.skyguide.map

import dev.dediamondpro.skyguide.utils.GuiUtils
import dev.dediamondpro.skyguide.utils.RenderUtils
import gg.essential.universal.UGraphics
import gg.essential.universal.UMinecraft
import gg.essential.universal.UMouse
import gg.essential.universal.UResolution
import org.lwjgl.input.Mouse

/**
 * @param images The images of the map
 * @param portals The portals on the map
 * @param topX The top left X coordinate in mc
 * @param topY The top left Y coordinate in mc
 * @param bottomX The bottom right X coordinate in mc
 * @param bottomY The bottom right Y coordinate in mc
 * @param xOffset The X offset of the player coordinates
 * @param yOffset The Y offset of the player coordinates
 */
@kotlinx.serialization.Serializable
data class Island(
    var images: MutableMap<Int, Textures>,
    val portals: MutableList<Portal> = mutableListOf(),
    val topX: Float,
    val topY: Float,
    val bottomX: Float,
    val bottomY: Float,
    val xOffset: Float = 0f,
    val yOffset: Float = 0f
) {
    val width = bottomX - topX
    val height = bottomY - topY
    var zone: String? = null
        private set
        get() {
            if (field == null) field = SkyblockMap.getZoneByIsland(this)
            return field
        }

    init {
        images = images.toSortedMap()
    }

    fun draw(y: Int, scale: Float) {
        getImage(y).draw(topX + xOffset, topY + yOffset, width, height)
        for (portal in portals) {
            if (portal.command == null) continue
            RenderUtils.drawImage(
                "/assets/skyguide/map_location.png",
                portal.x + xOffset - 16f / scale,
                portal.z + yOffset - 16f / scale,
                32f / scale,
                32f / scale
            )
            RenderUtils.drawImage(
                "/assets/skyguide/portal.png",
                portal.x + xOffset - 6f / scale,
                portal.z + yOffset - 9f / scale,
                12f / scale,
                18f / scale
            )
        }
    }

    fun drawLast(mouseX: Float, mouseY: Float) {
        for (portal in portals) {
            if (portal.command == null) continue
            if (mouseX >= (portal.x + xOffset - 16f) && mouseX <= (portal.x + xOffset + 16f)
                && mouseY >= (portal.z + yOffset - 16f) && mouseY <= (portal.z + yOffset + 16f)
            ) {
                val text = portal.name.split("\n").toMutableList()
                text.add("Left Click to teleport")
                text.add("Right Click to navigate")
                RenderUtils.drawToolTip(
                    text,
                    (mouseX.toInt()),
                    (mouseY.toInt()),
                    UResolution.windowWidth,
                    UResolution.windowHeight,
                    400,
                    UMinecraft.getFontRenderer()
                )
                if (GuiUtils.isClicked) UMinecraft.getMinecraft().thePlayer.sendChatMessage("/${portal.command}")
            }
        }
    }

    fun getImage(y: Int): Textures {
        for ((height, image) in images) {
            if (height >= y) return image
        }
        return images[0]!!
    }

    fun isInIsland(x: Float, y: Float): Boolean {
        return x >= topX + xOffset && y >= topY + yOffset
                && x <= bottomX + xOffset && y <= bottomY + yOffset
    }

    fun routeTo(island: String) {
        println(discoverPortals(island, this, mutableListOf()))
    }

    private fun discoverPortals(
        destination: String,
        currentIsland: Island,
        visitedIslands: MutableList<String>
    ): MutableList<Portal>? {
        visitedIslands.add(currentIsland.zone!!)
        for (portal in currentIsland.portals) {
            if (portal.destination == null || visitedIslands.contains(portal.destination)) continue
            if (portal.destination == destination) return mutableListOf(portal)
            val currentWorld = SkyblockMap.getCurrentWorld() ?: continue
            val destinationIsland = currentWorld[portal.destination] ?: continue
            val path = discoverPortals(destination, destinationIsland, visitedIslands) ?: continue
            path.add(0, portal)
            return path
        }
        return null
    }

    companion object {
        fun getXOffset(): Float {
            return SkyblockMap.getCurrentIsland()?.xOffset ?: 0f
        }

        fun getYOffset(): Float {
            return SkyblockMap.getCurrentIsland()?.yOffset ?: 0f
        }
    }
}

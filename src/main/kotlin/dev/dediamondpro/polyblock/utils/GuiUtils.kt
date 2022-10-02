package dev.dediamondpro.polyblock.utils

import gg.essential.universal.UMinecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.fml.common.eventhandler.EventPriority
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent

class GuiUtils {
    @SubscribeEvent
    fun onRenderEvent(event: RenderTickEvent) {
        if (event.phase != TickEvent.Phase.START) return
        if (time == -1L) {
            time = UMinecraft.getTime()
            return
        }
        val currentTime = UMinecraft.getTime()
        deltaTime += time - currentTime
        time = currentTime
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    fun onHudRenderEvent(event: RenderGameOverlayEvent) {
        if (event.type != RenderGameOverlayEvent.ElementType.ALL) return
        deltaTime = 0L
    }

    companion object {
        private var deltaTime: Long = 0L
        private var time = -1L

        fun getDeltaTime(): Long {
            return deltaTime
        }

        fun displayScreen(gui: GuiScreen) {
            TickDelay(0) {
                UMinecraft.getMinecraft().displayGuiScreen(gui)
            }
        }
    }
}
package dev.dediamondpro.skyguide.utils

import gg.essential.universal.UMinecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.fml.common.eventhandler.EventPriority
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent
import org.lwjgl.input.Mouse
import kotlin.math.absoluteValue

class GuiUtils {
    @SubscribeEvent
    fun onRenderEvent(event: RenderTickEvent) {
        if (event.phase != TickEvent.Phase.START) return
        if (time == -1L) {
            time = UMinecraft.getTime()
            return
        }
        val currentTime = UMinecraft.getTime()
        deltaTime += currentTime - time
        time = currentTime

        mouseDX = null
        mouseDY = null

        val leftClickedTemp = Mouse.isButtonDown(0)
        leftClicked = wasLeftClicked && !leftClickedTemp && leftMoveDelta <= 5
        wasLeftClicked = leftClickedTemp
        if (leftClickedTemp) leftMoveDelta += getMouseDX().absoluteValue + getMouseDY().absoluteValue
        else leftMoveDelta = 0

        val rightClickedTemp = Mouse.isButtonDown(1)
        rightClicked = wasRightClicked && !rightClickedTemp  && rightMoveDelta <= 5
        wasRightClicked = rightClickedTemp
        if (rightClickedTemp) rightMoveDelta += getMouseDX().absoluteValue + getMouseDY().absoluteValue
        else rightMoveDelta = 0
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    fun onHudRenderEvent(event: RenderGameOverlayEvent) {
        if (event.type != RenderGameOverlayEvent.ElementType.ALL) return
        deltaTime = 0L
    }

    companion object {
        private var deltaTime: Long = 0L
        private var time = -1L
        private var wasLeftClicked = false
        private var leftMoveDelta: Long = 0L
        private var wasRightClicked = false
        private var rightMoveDelta: Long = 0L
        private var mouseDX: Int? = null
        private var mouseDY: Int? = null
        var leftClicked = false
            private set
        var rightClicked = false
            private set

        fun getDeltaTime(): Long {
            return deltaTime
        }

        fun getMouseDX(): Int {
            if (mouseDX == null) mouseDX = Mouse.getDX()
            return mouseDX!!
        }

        fun getMouseDY(): Int {
            if (mouseDY == null) mouseDY = Mouse.getDY()
            return mouseDY!!
        }

        fun displayScreen(gui: GuiScreen?) {
            TickDelay(0) {
                UMinecraft.getMinecraft().displayGuiScreen(gui)
            }
        }
    }
}
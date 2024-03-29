package dev.dediamondpro.skyguide.utils

import gg.essential.universal.UMinecraft
import gg.essential.universal.UMouse
import net.minecraft.client.gui.GuiScreen
import net.minecraftforge.client.event.GuiScreenEvent
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.fml.common.eventhandler.EventPriority
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent
import org.lwjgl.input.Mouse
import kotlin.math.absoluteValue

class GuiUtils {
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    fun onRenderEvent(event: RenderTickEvent) {
        if (event.phase != Phase.START || event.type != TickEvent.Type.RENDER) return
        if (time == -1L) {
            time = UMinecraft.getTime()
            return
        }
        val currentTime = UMinecraft.getTime()
        hudDeltaTime += currentTime - time
        guiDeltaTime = currentTime - time
        time = currentTime
    }

    @SubscribeEvent
    fun onGuiRenderEvent(event: GuiScreenEvent.DrawScreenEvent.Pre) {
        val mouseX = Mouse.getX()
        val mouseY = Mouse.getY()
        mouseDX = mouseX - prevMouseX
        mouseDY = mouseY - prevMouseY
        prevMouseX = mouseX
        prevMouseY = mouseY

        val leftClickedTemp = Mouse.isButtonDown(0)
        leftClicked = wasLeftClicked && !leftClickedTemp && leftMoveDelta <= 15
        wasLeftClicked = leftClickedTemp
        if (leftClickedTemp) leftMoveDelta += mouseDX.absoluteValue + mouseDY.absoluteValue
        else leftMoveDelta = 0

        val rightClickedTemp = Mouse.isButtonDown(1)
        rightClicked = wasRightClicked && !rightClickedTemp && rightMoveDelta <= 15
        wasRightClicked = rightClickedTemp
        if (rightClickedTemp) rightMoveDelta += mouseDX.absoluteValue + mouseDY.absoluteValue
        else rightMoveDelta = 0
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    fun onHudRenderEvent(event: RenderGameOverlayEvent.Post) {
        if (event.type != RenderGameOverlayEvent.ElementType.ALL) return
        hudDeltaTime = 0L
    }

    companion object {
        private var hudDeltaTime: Long = 0L
        private var guiDeltaTime: Long = 0L
        private var time = -1L
        private var wasLeftClicked = false
        private var leftMoveDelta: Long = 0L
        private var wasRightClicked = false
        private var rightMoveDelta: Long = 0L
        private var prevMouseX = 0
        private var prevMouseY = 0
        var mouseDX: Int = 0
            private set
        var mouseDY: Int = 0
            private set
        var leftClicked = false
            private set
        var rightClicked = false
            private set

        fun getHudDeltaTime(): Long {
            return hudDeltaTime
        }

        fun getGuiDeltaTime(): Long {
            return guiDeltaTime
        }

        fun displayScreen(gui: GuiScreen?) {
            TickDelay(0) {
                UMinecraft.getMinecraft().displayGuiScreen(gui)
            }
        }

        fun mouseInArea(x: Number, y: Number, width: Number, height: Number): Boolean {
            return UMouse.Scaled.x >= x.toDouble() && UMouse.Scaled.x <= x.toDouble() + width.toDouble()
                    && UMouse.Scaled.y >= y.toDouble() && UMouse.Scaled.y <= y.toDouble() + height.toDouble()
        }
    }
}
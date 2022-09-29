package dev.dediamondpro.polyblock.utils

import cc.polyfrost.oneconfig.events.event.HudRenderEvent
import cc.polyfrost.oneconfig.events.event.RenderEvent
import cc.polyfrost.oneconfig.events.event.Stage
import cc.polyfrost.oneconfig.libs.eventbus.Subscribe
import cc.polyfrost.oneconfig.utils.gui.GuiUtils

class HudDeltaTime {
    @Subscribe
    fun onRenderEvent(event: RenderEvent) {
        if (event.stage != Stage.START) return
        deltaTime += GuiUtils.getDeltaTime()
    }

    @Subscribe(priority = Int.MIN_VALUE)
    fun onHudRenderEvent(event: HudRenderEvent) {
        deltaTime = 0f
    }

    companion object {
        var deltaTime: Float = 0f

        fun get(): Float {
            return deltaTime
        }
    }
}
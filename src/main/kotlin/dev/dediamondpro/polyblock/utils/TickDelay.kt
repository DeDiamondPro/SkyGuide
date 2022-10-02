package dev.dediamondpro.polyblock.utils

import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent

class TickDelay(private var delay: Int, private val action: Runnable) {
    init {
        MinecraftForge.EVENT_BUS.register(this)
    }

    @SubscribeEvent
    fun onTick(event: ClientTickEvent) {
        if (delay > 0) {
            delay--
        } else {
            action.run()
            MinecraftForge.EVENT_BUS.unregister(this)
        }
    }
}
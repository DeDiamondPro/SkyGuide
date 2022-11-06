package dev.dediamondpro.skyguide.handlers

import dev.dediamondpro.skyguide.config.Config
import gg.essential.universal.UResolution
import gg.essential.universal.wrappers.UPlayer
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent

class FirstLaunchHandler {

    @SubscribeEvent
    fun onTick(event: ClientTickEvent) {
        if (!UPlayer.hasPlayer()) return
        if (Config.firstLaunchVersion < 1) Config.miniMapScale = (2f / UResolution.scaleFactor).toFloat()
        Config.firstLaunchVersion = CURRENT_VERSION
        Config.markDirty()
        Config.writeData()
        MinecraftForge.EVENT_BUS.unregister(this)
    }

    companion object {
        const val CURRENT_VERSION = 1
    }
}
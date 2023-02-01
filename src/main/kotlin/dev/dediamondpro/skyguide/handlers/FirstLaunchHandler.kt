package dev.dediamondpro.skyguide.handlers

import cc.polyfrost.oneconfig.utils.Notifications
import dev.dediamondpro.skyguide.config.Config
import dev.dediamondpro.skyguide.utils.SBInfo
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent

class FirstLaunchHandler {
    @SubscribeEvent
    fun clientTickEvent(e: ClientTickEvent) {
        if (Config.firstLaunchVersion != 1 && SBInfo.inSkyblock) {
            Notifications.INSTANCE.send(
                "Thank you for installing SkyGuide!",
                "Press '${Config.mapKeyBind.display}' to open the map!\nDo '/skyguide' to configure skyguide!",
                10000f,
                Runnable { Config.openGui() }
            )
            Config.firstLaunchVersion = 1
            Config.save()
        }
    }
}
package dev.dediamondpro.skyguide.handlers

import cc.polyfrost.oneconfig.libs.universal.UChat
import dev.dediamondpro.skyguide.config.Config
import dev.dediamondpro.skyguide.utils.SBInfo
import net.minecraft.util.EnumChatFormatting
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent

class FirstLaunchHandler {
    @SubscribeEvent
    fun clientTickEvent(e: ClientTickEvent) {
        if (Config.firstLaunchVersion != 1 && SBInfo.inSkyblock) {
            UChat.chat(
                "${EnumChatFormatting.YELLOW}Thank you for installing SkyGuide!" +
                        "\n${EnumChatFormatting.YELLOW}SkyGuide provides a neat mini-map" +
                        "\n${EnumChatFormatting.YELLOW}and a full map you can open by pressing m!\n" +
                        "${EnumChatFormatting.YELLOW}To configure SkyGuide please do /skyguide!"
            )
            Config.firstLaunchVersion = 1
            Config.save()
        }
    }
}
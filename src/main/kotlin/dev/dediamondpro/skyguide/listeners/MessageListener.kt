package dev.dediamondpro.skyguide.listeners

import cc.polyfrost.oneconfig.libs.universal.UScreen
import dev.dediamondpro.skyguide.gui.MapGui
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

class MessageListener {

    @SubscribeEvent
    fun onMessageReceive(event: ClientChatReceivedEvent) {
        if (event.message.unformattedText.equals("Warping...") && UScreen.currentScreen is MapGui) UScreen.displayScreen(
            null
        )
    }
}
package dev.dediamondpro.skyguide.listeners

import dev.dediamondpro.skyguide.gui.MapGui
import gg.essential.universal.UScreen
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
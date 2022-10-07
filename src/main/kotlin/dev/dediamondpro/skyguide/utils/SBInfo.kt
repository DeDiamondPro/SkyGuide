package dev.dediamondpro.skyguide.utils

import dev.dediamondpro.skyguide.config.Config
import dev.dediamondpro.skyguide.handlers.AssetHandler
import gg.essential.universal.UMinecraft
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import java.util.*

class SBInfo {
    companion object {
        var inSkyblock = false
            private set
        var locraw: LocrawObject? = null
            private set
        var zone: String = "unknown"
            private set

        fun onHypixel(): Boolean {
            return if (UMinecraft.getMinecraft().theWorld != null && UMinecraft.getMinecraft().thePlayer != null
                && UMinecraft.getMinecraft().thePlayer.clientBrand != null
            ) {
                UMinecraft.getMinecraft().thePlayer.clientBrand.lowercase(Locale.getDefault()).contains("hypixel")
            } else false
        }
    }

    private val json = Json { ignoreUnknownKeys = true }
    private var lastSwap = -1L
    private var lastLocraw = -1L

    @SubscribeEvent(receiveCanceled = true)
    fun onMessage(event: ClientChatReceivedEvent) {
        if (!onHypixel()) return
        val message = event.message.unformattedText
        if (message.startsWith("{") && message.endsWith("}")) {
            try {
                locraw = json.decodeFromString<LocrawObject>(message)
                if (lastLocraw != -1L && !event.isCanceled) {
                    event.isCanceled = true
                    lastLocraw = -1L
                }
                if (locraw!!.gametype == "SKYBLOCK") {
                    inSkyblock = true
                    if (!AssetHandler.downloadedAssets) AssetHandler.initialize()
                    zone = locraw!!.mode
                } else {
                    inSkyblock = false
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    @SubscribeEvent
    fun onWorldChange(event: WorldEvent.Load) {
        if (!onHypixel()) {
            inSkyblock = false
            return
        }
        if (!Config.keepAssetsLoaded) AssetHandler.unloadAssets()
        inSkyblock = false
        zone = "unknown"
        lastSwap = UMinecraft.getTime()
        lastLocraw = -1L
        locraw = null
    }

    @SubscribeEvent
    fun onTick(event: TickEvent.ClientTickEvent) {
        if (!onHypixel() || event.phase != TickEvent.Phase.START) return
        val currentTime = UMinecraft.getTime()
        if (locraw == null && currentTime - lastSwap > 1500 && currentTime - lastLocraw > 20000) {
            UMinecraft.getMinecraft().thePlayer.sendChatMessage("/locraw")
            lastLocraw = currentTime
        }
    }
}

@Serializable
data class LocrawObject(
    val server: String,
    val gametype: String = "unknown",
    val mode: String = "unknown",
    val map: String = "unknown"
)
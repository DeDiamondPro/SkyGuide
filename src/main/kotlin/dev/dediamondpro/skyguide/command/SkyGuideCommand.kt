package dev.dediamondpro.skyguide.command

import dev.dediamondpro.skyguide.config.Config
import dev.dediamondpro.skyguide.gui.MapGui
import dev.dediamondpro.skyguide.map.poi.Npc
import dev.dediamondpro.skyguide.utils.GuiUtils
import dev.dediamondpro.skyguide.utils.roundTo
import gg.essential.universal.UDesktop
import gg.essential.universal.UMinecraft
import gg.essential.universal.wrappers.UPlayer
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import net.minecraft.client.entity.AbstractClientPlayer
import net.minecraft.command.CommandBase
import net.minecraft.command.ICommandSender
import net.minecraft.entity.player.EntityPlayer

class SkyGuideCommand : CommandBase() {
    private val json = Json { prettyPrint = true; encodeDefaults = true }

    override fun getCommandName(): String {
        return "skyguide"
    }

    override fun getCommandUsage(sender: ICommandSender): String {
        return "/skyguide"
    }

    override fun canCommandSenderUseCommand(sender: ICommandSender?): Boolean {
        return true
    }

    override fun processCommand(sender: ICommandSender, args: Array<String>) {
        if (args.isEmpty()) {
            GuiUtils.displayScreen(Config.gui()!!)
            return
        }
        when (args[0]) {
            "map" -> GuiUtils.displayScreen(MapGui())
            "getnpc" -> {
                val players = UMinecraft.getWorld()!!.playerEntities
                var lowestDistPlayer: EntityPlayer? = null
                var lowestDist = Double.MAX_VALUE
                for (player in players) {
                    if (!AbstractClientPlayer::class.java.isAssignableFrom(player.javaClass) || player == UMinecraft.getPlayer()) continue
                    val dist = player.getDistanceSq(UPlayer.getPosX(), UPlayer.getPosY(), UPlayer.getPosZ())
                    if (dist < lowestDist) {
                        lowestDistPlayer = player
                        lowestDist = dist
                    }
                }
                if (lowestDistPlayer == null) return
                for (string in lowestDistPlayer.gameProfile.properties.keySet()) {
                    for (property in lowestDistPlayer.gameProfile.properties.get(string)) {
                        if (property.name == "textures") {
                            UDesktop.setClipboardString(
                                json.encodeToString(
                                    Json.serializersModule.serializer(),
                                    Npc(
                                        "",
                                        null,
                                        lowestDistPlayer.uniqueID.toString(),
                                        property.value,
                                        lowestDistPlayer.posX.roundTo(1).toFloat(),
                                        lowestDistPlayer.posY.roundTo(1).toFloat(),
                                        lowestDistPlayer.posZ.roundTo(1).toFloat()
                                    )
                                )
                            )
                            return
                        }
                    }
                }
            }
        }
    }
}
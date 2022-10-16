package dev.dediamondpro.skyguide.command

import dev.dediamondpro.skyguide.config.Config
import dev.dediamondpro.skyguide.gui.MapGui
import dev.dediamondpro.skyguide.gui.NpcGui
import dev.dediamondpro.skyguide.map.poi.Npc
import dev.dediamondpro.skyguide.utils.GuiUtils
import dev.dediamondpro.skyguide.utils.roundTo
import gg.essential.universal.UChat
import gg.essential.universal.UDesktop
import gg.essential.universal.UMinecraft
import gg.essential.universal.wrappers.UPlayer
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import net.minecraft.client.entity.AbstractClientPlayer
import net.minecraft.command.CommandBase
import net.minecraft.command.ICommandSender
import net.minecraft.entity.item.EntityArmorStand
import net.minecraft.entity.player.EntityPlayer
import java.util.regex.Pattern

class SkyGuideCommand : CommandBase() {
    private val json = Json { prettyPrint = true }

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
            "getnpcs" -> {
                NpcGui.collectingNpcs = !NpcGui.collectingNpcs
                if (!NpcGui.collectingNpcs) GuiUtils.displayScreen(NpcGui())
                else UChat.chat("Started collection of npcs")
            }

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
                UDesktop.setClipboardString(
                    json.encodeToString(
                        Json.serializersModule.serializer(),
                        NpcGui.getNpc(lowestDistPlayer)
                    )
                )
            }
        }
    }
}
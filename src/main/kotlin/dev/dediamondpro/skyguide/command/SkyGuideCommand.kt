package dev.dediamondpro.skyguide.command

import dev.dediamondpro.skyguide.config.Config
import dev.dediamondpro.skyguide.gui.MapGui
import dev.dediamondpro.skyguide.gui.NpcGui
import dev.dediamondpro.skyguide.map.Island
import dev.dediamondpro.skyguide.map.SkyblockMap
import dev.dediamondpro.skyguide.map.navigation.Destination
import dev.dediamondpro.skyguide.map.navigation.NavigationHandler
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
import net.minecraft.util.EnumChatFormatting
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
            "waypoint" -> {
                if (args.size < 3) {
                    UChat.chat("${EnumChatFormatting.RED}Please specify the coordinates!")
                    return
                }
                if (SkyblockMap.getCurrentWorld() == null) return
                val x = args[1].toFloat()
                val z = args[if (args.size == 3) 2 else 3].toFloat()
                var island: Island? = if (SkyblockMap.getCurrentIsland()
                        ?.isInIsland(x, z) == true
                ) SkyblockMap.getCurrentIsland() else null
                if (island == null) {
                    for (i in SkyblockMap.getCurrentWorld()!!.values) {
                        if (!i.isInIsland(x, z)) continue
                        island = i
                        break
                    }
                }
                if (island == null) island = SkyblockMap.getCurrentIsland() ?: return
                NavigationHandler.navigateTo(
                    Destination(
                        island,
                        x,
                        if (args.size == 3) null else args[2].toFloat(),
                        z
                    )
                )
            }

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
                val npc = NpcGui.getNpc(lowestDistPlayer) ?: return
                if (npc.name.isEmpty()) {
                    GuiUtils.displayScreen(NpcGui(mutableListOf(npc)))
                    return
                }
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
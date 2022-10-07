package dev.dediamondpro.skyguide.command

import dev.dediamondpro.skyguide.config.Config
import dev.dediamondpro.skyguide.gui.MapGui
import dev.dediamondpro.skyguide.utils.GuiUtils
import net.minecraft.command.CommandBase
import net.minecraft.command.ICommandSender

class SkyGuideCommand : CommandBase() {
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
        }
    }
}
package dev.dediamondpro.polyblock.command

import dev.dediamondpro.polyblock.config.BlockConfig
import dev.dediamondpro.polyblock.gui.MapGui
import dev.dediamondpro.polyblock.utils.GuiUtils
import net.minecraft.command.CommandBase
import net.minecraft.command.ICommandSender

class PolyCommand : CommandBase() {
    override fun getCommandName(): String {
        return "polyblock"
    }

    override fun getCommandUsage(sender: ICommandSender): String {
        return "/polyblock"
    }

    override fun canCommandSenderUseCommand(sender: ICommandSender?): Boolean {
        return true
    }

    override fun processCommand(sender: ICommandSender, args: Array<String>) {
        if (args.isEmpty()) {
            GuiUtils.displayScreen(BlockConfig.gui()!!)
            return
        }
        when (args[0]) {
            "map" -> GuiUtils.displayScreen(MapGui())
        }
    }
}
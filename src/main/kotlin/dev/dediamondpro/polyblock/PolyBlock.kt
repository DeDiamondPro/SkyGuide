package dev.dediamondpro.polyblock

import dev.dediamondpro.polyblock.command.PolyCommand
import dev.dediamondpro.polyblock.config.BlockConfig
import dev.dediamondpro.polyblock.handlers.AssetHandler
import dev.dediamondpro.polyblock.handlers.KeyBindHandler
import dev.dediamondpro.polyblock.handlers.WaypointHandler
import dev.dediamondpro.polyblock.utils.GuiUtils
import dev.dediamondpro.polyblock.utils.SBInfo
import net.minecraftforge.client.ClientCommandHandler
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import java.io.File

@Mod(
    modid = PolyBlock.ID,
    name = PolyBlock.NAME,
    version = PolyBlock.VER,
    modLanguageAdapter = "gg.essential.api.utils.KotlinAdapter"
)
object PolyBlock {
    const val NAME = "@NAME@"
    const val VER = "@VER@"
    const val ID = "@ID@"

    @Mod.EventHandler
    fun onInitialization(event: FMLInitializationEvent) {
        File("./config/$ID").mkdirs()
        BlockConfig.preload()
        if (BlockConfig.downloadAtLaunch) AssetHandler.initialize()
        KeyBindHandler.init()
        MinecraftForge.EVENT_BUS.register(SBInfo())
        MinecraftForge.EVENT_BUS.register(GuiUtils())
        MinecraftForge.EVENT_BUS.register(WaypointHandler())
        ClientCommandHandler.instance.registerCommand(PolyCommand())
    }
}
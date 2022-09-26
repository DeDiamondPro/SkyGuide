package dev.dediamondpro.polyblock

import cc.polyfrost.oneconfig.events.EventManager
import dev.dediamondpro.polyblock.config.BlockConfig
import dev.dediamondpro.polyblock.handlers.WaypointHandler
import dev.dediamondpro.polyblock.utils.AssetHandler
import dev.dediamondpro.polyblock.utils.SBInfo
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent

@Mod(
    modid = PolyBlock.ID,
    name = PolyBlock.NAME,
    version = PolyBlock.VER,
    modLanguageAdapter = "cc.polyfrost.oneconfig.utils.KotlinLanguageAdapter"
)
object PolyBlock {
    const val NAME = "@NAME@"
    const val VER = "@VER@"
    const val ID = "@ID@"

    @Mod.EventHandler
    fun onInitialization(event: FMLInitializationEvent) {
        if (BlockConfig.downloadAtLaunch) AssetHandler.initialize()
        EventManager.INSTANCE.eventBus.register(SBInfo())
        MinecraftForge.EVENT_BUS.register(WaypointHandler())
    }
}
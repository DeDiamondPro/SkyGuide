package cc.polyfrost.polyblock

import cc.polyfrost.oneconfig.events.EventManager
import cc.polyfrost.polyblock.config.BlockConfig
import cc.polyfrost.polyblock.map.SkyblockMap
import cc.polyfrost.polyblock.utils.NetworkUtils
import cc.polyfrost.polyblock.handlers.WaypointHandler
import cc.polyfrost.polyblock.utils.SBInfo
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
        BlockConfig
        //NetworkUtils.loadCertificate()
        EventManager.INSTANCE.eventBus.register(SBInfo())
        MinecraftForge.EVENT_BUS.register(WaypointHandler())
    }
}
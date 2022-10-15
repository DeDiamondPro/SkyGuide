package dev.dediamondpro.skyguide

import com.mojang.authlib.GameProfile
import com.mojang.authlib.minecraft.MinecraftProfileTexture
import dev.dediamondpro.skyguide.config.Config
import dev.dediamondpro.skyguide.handlers.AssetHandler
import dev.dediamondpro.skyguide.handlers.KeyBindHandler
import dev.dediamondpro.skyguide.hud.MiniMap
import dev.dediamondpro.skyguide.map.navigation.NavigationHandler
import dev.dediamondpro.skyguide.utils.GuiUtils
import dev.dediamondpro.skyguide.utils.SBInfo
import net.minecraft.client.Minecraft
import net.minecraftforge.client.ClientCommandHandler
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import java.io.File

@Mod(
    modid = SkyGuide.ID,
    name = SkyGuide.NAME,
    version = SkyGuide.VER,
    modLanguageAdapter = "gg.essential.api.utils.KotlinAdapter"
)
object SkyGuide {
    const val NAME = "@NAME@"
    const val VER = "@VER@"
    const val ID = "@ID@"

    @Mod.EventHandler
    fun onInitialization(event: FMLInitializationEvent) {
        File("./config/$ID").mkdirs()
        Config.preload()
        if (Config.downloadAtLaunch) AssetHandler.initialize()
        KeyBindHandler.init()
        MinecraftForge.EVENT_BUS.register(SBInfo())
        MinecraftForge.EVENT_BUS.register(GuiUtils())
        MinecraftForge.EVENT_BUS.register(MiniMap())
        MinecraftForge.EVENT_BUS.register(NavigationHandler())
        ClientCommandHandler.instance.registerCommand(dev.dediamondpro.skyguide.command.SkyGuideCommand())
    }
}
package dev.dediamondpro.skyguide.handlers

import dev.dediamondpro.skyguide.SkyGuide
import dev.dediamondpro.skyguide.gui.MapGui
import dev.dediamondpro.skyguide.map.SkyblockMap
import dev.dediamondpro.skyguide.utils.GuiUtils
import dev.dediamondpro.skyguide.utils.SBInfo
import gg.essential.universal.UKeyboard
import net.minecraft.client.settings.KeyBinding
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.client.registry.ClientRegistry.registerKeyBinding
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent

object KeyBindHandler {
    private val keybindings = mapOf(
        KeyBinding("Open Map", UKeyboard.KEY_M, SkyGuide.NAME) to {
            if (SBInfo.inSkyblock) GuiUtils.displayScreen(MapGui())
        }
    )

    fun init() {
        MinecraftForge.EVENT_BUS.register(this)
        keybindings.forEach { (keybinding) ->
            registerKeyBinding(keybinding)
        }
    }

    @SubscribeEvent
    fun onKeyPressed(event: KeyInputEvent) {
        keybindings.forEach { (keybinding, action) ->
            if (keybinding.isPressed) action()
        }
    }
}
package dev.dediamondpro.polyblock.handlers

import dev.dediamondpro.polyblock.PolyBlock
import dev.dediamondpro.polyblock.gui.MapGui
import dev.dediamondpro.polyblock.map.SkyblockMap
import dev.dediamondpro.polyblock.utils.GuiUtils
import dev.dediamondpro.polyblock.utils.SBInfo
import gg.essential.universal.UKeyboard
import net.minecraft.client.settings.KeyBinding
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.client.registry.ClientRegistry.registerKeyBinding
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent

object KeyBindHandler {
    private val keybindings = mapOf(
        KeyBinding("Open Map", UKeyboard.KEY_M, PolyBlock.NAME) to {
            if (SBInfo.inSkyblock && SkyblockMap.currentWorldAvailable()) GuiUtils.displayScreen(MapGui())
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
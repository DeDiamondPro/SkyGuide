package cc.polyfrost.polyblock.config

import cc.polyfrost.oneconfig.config.Config
import cc.polyfrost.oneconfig.config.annotations.Dropdown
import cc.polyfrost.oneconfig.config.annotations.HUD
import cc.polyfrost.oneconfig.config.annotations.KeyBind
import cc.polyfrost.oneconfig.config.annotations.Slider
import cc.polyfrost.oneconfig.config.annotations.Switch
import cc.polyfrost.oneconfig.config.core.OneKeyBind
import cc.polyfrost.oneconfig.config.data.Mod
import cc.polyfrost.oneconfig.config.data.ModType
import cc.polyfrost.oneconfig.libs.universal.UKeyboard
import cc.polyfrost.oneconfig.utils.gui.GuiUtils
import cc.polyfrost.polyblock.gui.MapGui
import cc.polyfrost.polyblock.hud.MiniMap
import cc.polyfrost.polyblock.utils.SBInfo

object BlockConfig : Config(Mod("PolyBlock", ModType.SKYBLOCK), "polyblock.json") {

    @Dropdown(
        name = "Texture Quality",
        options = ["low", "medium", "high"],
        category = "General"
    )
    var textureQuality = 1

    @Switch(name = "Keep In Memory")
    var keepAssetsLoaded = true

    @KeyBind(name = "Map Keybind", category = "Map")
    var mapKeyBind = OneKeyBind(UKeyboard.KEY_M)

    @Slider(
        name = "Default Scale",
        category = "Map",
        min = 0.25f, max = 5f
    )
    var defaultScale = 2f

    @Slider(
        name = "Player Pointer Size",
        category = "Map",
        min = 7f, max = 49f
    )
    var pointerSize = 14f

    @HUD(name = "Mini Map", category = "Mini Map")
    var miniMap = MiniMap()

    init {
        initialize()
        //registerKeyBind(mapKeyBind) { if (SBInfo.inSkyblock) GuiUtils.displayScreen(MapGui()) }
        registerKeyBind(mapKeyBind) { GuiUtils.displayScreen(MapGui()) }
    }
}
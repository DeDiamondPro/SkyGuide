package cc.polyfrost.polyblock.config

import cc.polyfrost.oneconfig.config.Config
import cc.polyfrost.oneconfig.config.annotations.KeyBind
import cc.polyfrost.oneconfig.config.core.OneKeyBind
import cc.polyfrost.oneconfig.config.data.Mod
import cc.polyfrost.oneconfig.config.data.ModType
import cc.polyfrost.oneconfig.libs.universal.UKeyboard
import cc.polyfrost.oneconfig.utils.gui.GuiUtils
import cc.polyfrost.polyblock.gui.MapGui

object BlockConfig : Config(Mod("PolyBlock", ModType.SKYBLOCK), "polyblock.json") {

    @KeyBind(name = "Map Keybind")
    var mapKeyBind = OneKeyBind(UKeyboard.KEY_M)

    init {
        initialize()
        registerKeyBind(mapKeyBind) { GuiUtils.displayScreen(MapGui()) }
    }
}
package dev.dediamondpro.polyblock.config

import cc.polyfrost.oneconfig.config.Config
import cc.polyfrost.oneconfig.config.annotations.Dropdown
import cc.polyfrost.oneconfig.config.annotations.HUD
import cc.polyfrost.oneconfig.config.annotations.KeyBind
import cc.polyfrost.oneconfig.config.annotations.NonProfileSpecific
import cc.polyfrost.oneconfig.config.annotations.Slider
import cc.polyfrost.oneconfig.config.annotations.Switch
import cc.polyfrost.oneconfig.config.core.OneKeyBind
import cc.polyfrost.oneconfig.config.data.Mod
import cc.polyfrost.oneconfig.config.data.ModType
import cc.polyfrost.oneconfig.libs.universal.UKeyboard
import cc.polyfrost.oneconfig.renderer.RenderManager
import cc.polyfrost.oneconfig.utils.gui.GuiUtils
import dev.dediamondpro.polyblock.gui.MapGui
import dev.dediamondpro.polyblock.hud.MiniMap
import dev.dediamondpro.polyblock.map.SkyblockMap
import dev.dediamondpro.polyblock.utils.AssetHandler
import dev.dediamondpro.polyblock.utils.SBInfo
import dev.dediamondpro.polyblock.utils.Waypoint

object BlockConfig : Config(Mod("PolyBlock", ModType.SKYBLOCK), "polyblock.json") {

    @Dropdown(
        name = "Texture Quality",
        description = "The quality of the textures.",
        options = ["low", "medium", "high"],
        category = "General"
    )
    @NonProfileSpecific
    var textureQuality = 1

    @Switch(
        name = "Keep In Memory",
        description = "Keep all assets in memory."
    )
    @NonProfileSpecific
    var keepAssetsLoaded = true

    @Switch(
        name = "Download at launch",
        description = "Download all assets at launch."
    )
    @NonProfileSpecific
    var downloadAtLaunch = false

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

    var waypoints: ArrayList<Waypoint> = ArrayList()

    init {
        initialize()
        registerKeyBind(mapKeyBind) {
            if (enabled && SBInfo.inSkyblock && SkyblockMap.currentWorldAvailable()) GuiUtils.displayScreen(
                MapGui()
            )
        }
        addListener("smooth") { RenderManager.setupAndDraw { AssetHandler.unloadAssets(it) } }
        addListener("textureQuality") {
            if (AssetHandler.downloadedAssets) {
                RenderManager.setupAndDraw { AssetHandler.unloadAssets(it) }
                AssetHandler.updateTextures()
            }
        }
    }
}
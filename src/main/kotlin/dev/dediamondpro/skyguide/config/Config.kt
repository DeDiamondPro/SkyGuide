package dev.dediamondpro.skyguide.config

import dev.dediamondpro.skyguide.SkyGuide
import dev.dediamondpro.skyguide.handlers.AssetHandler
import dev.dediamondpro.skyguide.utils.TickDelay
import dev.dediamondpro.skyguide.utils.Waypoint
import dev.dediamondpro.skyguide.utils.toFile
import gg.essential.vigilance.Vigilant
import gg.essential.vigilance.data.Property
import gg.essential.vigilance.data.PropertyType

object Config : Vigilant("./config/${SkyGuide.ID}/config.toml".toFile(), SkyGuide.NAME) {

    @Property(
        type = PropertyType.SELECTOR,
        name = "Texture Quality",
        description = "The quality of the textures.",
        options = ["low", "medium", "high"],
        category = "General"
    )
    var textureQuality = 1

    @Property(
        type = PropertyType.SWITCH,
        name = "Keep In Memory",
        description = "Keep all assets in memory.",
        category = "General"
    )
    var keepAssetsLoaded = true

    @Property(
        type = PropertyType.SWITCH,
        name = "Download at launch",
        description = "Download all assets at launch.",
        category = "General"
    )
    var downloadAtLaunch = false

    @Property(
        type = PropertyType.SWITCH,
        name = "Lazy Loading",
        description = "Load assets as they are needed.",
        category = "General"
    )
    var lazyLoading = true

    /*@KeyBind(
        name = "Map Keybind",
        description = "The keybind to open the map.",
        category = "Map"
    )
    var mapKeyBind = OneKeyBind(UKeyboard.KEY_M)*/

    @Property(
        type = PropertyType.DECIMAL_SLIDER,
        name = "Default Scale",
        description = "The default scale of the map.",
        category = "Map",
        minF = 0.25f, maxF = 5f
    )
    var defaultScale = 2f

    @Property(
        type = PropertyType.DECIMAL_SLIDER,
        name = "Player Pointer Size",
        description = "The size of the player pointer.",
        category = "Map",
        minF = 7f, maxF = 49f
    )
    var pointerSize = 14f

    /*@HUD(name = "Mini Map", category = "Mini Map")
    var miniMap = MiniMap()*/

    var waypoints: ArrayList<Waypoint> = ArrayList()

    // Hidden field only meant for dev testing
    @Property(
        type = PropertyType.SWITCH,
        category = "General",
        name = "Disable asset downloading",
        hidden = true
    )
    var downloadAssets = true

    init {
        initialize()
        registerListener("textureQuality") { _: Any ->
            TickDelay(1) {
                if (AssetHandler.downloadedAssets) {
                    AssetHandler.updateTextures()
                }
            }
        }
        addDependency("lazyLoading", "keepAssetsLoaded")
    }
}
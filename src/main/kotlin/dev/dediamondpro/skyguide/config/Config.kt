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

    // General

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

    // Map

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
    var mapPointerSize = 14f

    // Mini-Map

    @Property(
        type = PropertyType.SWITCH,
        name = "Enable Mini-Map",
        description = "Enable the mini-map.",
        category = "Mini-Map"
    )
    var miniMapEnabled = true

    @Property(
        type = PropertyType.SELECTOR,
        name = "Mini-Map Location",
        description = "The location of the mini-map.",
        options = ["Top Left", "Top Right", "Bottom Left", "Bottom Right"],
        category = "Mini-Map"
    )
    var miniMapLocation = 1

    @Property(
        type = PropertyType.DECIMAL_SLIDER,
        name = "Scale",
        description = "The scale of the mini-map.",
        category = "Mini-Map",
        minF = 0.25f, maxF = 5f
    )
    var miniMapScale = 1f

    @Property(
        type = PropertyType.SWITCH,
        name = "Rotate With Player",
        description = "Rotate the map with the player.",
        category = "Mini-Map"
    )
    var rotateWithPlayer = true

    @Property(
        type = PropertyType.DECIMAL_SLIDER,
        name = "Zoom Factor",
        description = "The zoom factor of the map.",
        minF = 0.25f, maxF = 5f,
        category = "Mini-Map"
    )
    var mapZoom = 1f

    @Property(
        type = PropertyType.DECIMAL_SLIDER,
        name = "Player Pointer Size",
        description = "The size of the player pointer.",
        minF = 3.5f, maxF = 35f,
        category = "Mini-Map"
    )
    var miniMapPointerSize = 7f

    @Property(
        type = PropertyType.SWITCH,
        name = "Show in GUIs",
        description = "Show the mini-map in GUIs.",
        category = "Mini-Map"
    )
    var showInGUIs = false

    @Property(
        type = PropertyType.SWITCH,
        name = "Show in F3",
        description = "Show the mini-map in the F3 screen.",
        category = "Mini-Map"
    )
    var showInF3 = false

    @Property(
        type = PropertyType.SWITCH,
        name = "Show in Chat",
        description = "Show the mini-map while the chat is opened.",
        category = "Mini-Map"
    )
    var showInChat = true

    // Hidden

    var waypoints: ArrayList<Waypoint> = ArrayList()

    @Property(
        type = PropertyType.SWITCH,
        category = "General",
        name = "Disable asset downloading",
        hidden = true
    )
    var downloadAssets = true

    init {
        initialize()
        InternalConfig.initialize()
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
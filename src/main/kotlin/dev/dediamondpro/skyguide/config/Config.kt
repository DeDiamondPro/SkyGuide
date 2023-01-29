package dev.dediamondpro.skyguide.config

import dev.dediamondpro.skyguide.SkyGuide
import dev.dediamondpro.skyguide.handlers.AssetHandler
import dev.dediamondpro.skyguide.utils.TickDelay
import gg.essential.vigilance.Vigilant
import gg.essential.vigilance.data.Property
import gg.essential.vigilance.data.PropertyType
import java.awt.Color
import java.io.File

private val configFile by lazy {
    val parent = File("config", "skyguide")
    if (!parent.exists() && !parent.mkdirs())
        throw IllegalStateException("Could not create config directory.")
    File(parent, "skyguide.toml")
}

object Config : Vigilant(configFile, SkyGuide.NAME) {
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

    @Property(
        type = PropertyType.SWITCH,
        name = "Show MVP Warps",
        description = "Show MVP warps on the map.",
        category = "Map",
    )
    var showMVPWarps = true

    @Property(
        type = PropertyType.SWITCH,
        name = "Show NPCs",
        description = "Show npcs on the map.",
        category = "Map",
    )
    var showNpcs = true

    @Property(
        type = PropertyType.COLOR,
        allowAlpha = false,
        name = "Pin Color",
        description = "The color of the destination pin.",
        category = "Map"
    )
    var pinColor = Color.RED

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
    var miniMapScale = 0.70f

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
    var mapZoom = 1.5f

    @Property(
        type = PropertyType.DECIMAL_SLIDER,
        name = "Underground Zoom Multiplier",
        description = "The zoom multiplier of the map when underground.",
        minF = 0.25f, maxF = 5f,
        category = "Mini-Map"
    )
    var undergroundMapZoom = 2f

    @Property(
        type = PropertyType.SWITCH,
        name = "Background",
        description = "Whether the map has a background",
        category = "Mini-Map"
    )
    var background = false

    @Property(
        type = PropertyType.COLOR,
        name = "Background Color",
        description = "The color of the background",
        category = "Mini-Map"
    )
    var backgroundColor = Color(0, 0, 0)

    @Property(
        type = PropertyType.SWITCH,
        name = "Show PIOs",
        description = "Whether to show points of interests (npcs, portals, ...) on the mini-map.",
        category = "Mini-Map"
    )
    var showPIOs = true

    @Property(
        type = PropertyType.DECIMAL_SLIDER,
        name = "Player Pointer Size",
        description = "The size of the player pointer.",
        minF = 3.5f, maxF = 35f,
        category = "Mini-Map"
    )
    var miniMapPointerSize = 12f

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

    @Property(
        type = PropertyType.SWITCH,
        name = "Smooth images",
        description = "Smooth the images using linear scaling.\nCan reduce flicker of the mini-map on some monitors but might look worse.",
        category = "Mini-Map"
    )
    var smoothImages = false

    // Integration

    @Property(
        type = PropertyType.SWITCH,
        name = "Show Skytils Waypoints",
        description = "Show Skytils waypoints on the map.",
        category = "Integration",
        subcategory = "Skytils"
    )
    var skytilsWaypoints = true

    @Property(
        type = PropertyType.SWITCH,
        name = "Show Disabled Skytils Waypoints",
        description = "Whether to show disabled Skytils waypoints on the map.",
        category = "Integration",
        subcategory = "Skytils"
    )
    var disabledSkytilsWaypoints = false

    // Hidden

    @Property(
        type = PropertyType.SWITCH,
        category = "Hidden",
        name = "Download Assets",
        hidden = true
    )
    var downloadAssets = true

    @Property(
        type = PropertyType.SLIDER,
        name = "First Launch Version",
        category = "Hidden",
        hidden = true
    )
    var firstLaunchVersion = 0

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
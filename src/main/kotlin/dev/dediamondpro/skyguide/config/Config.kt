package dev.dediamondpro.skyguide.config

import cc.polyfrost.oneconfig.config.Config
import cc.polyfrost.oneconfig.config.annotations.Dropdown
import cc.polyfrost.oneconfig.config.annotations.HUD
import cc.polyfrost.oneconfig.config.annotations.KeyBind
import cc.polyfrost.oneconfig.config.annotations.NonProfileSpecific
import cc.polyfrost.oneconfig.config.annotations.Slider
import cc.polyfrost.oneconfig.config.annotations.Switch
import cc.polyfrost.oneconfig.config.core.OneColor
import cc.polyfrost.oneconfig.config.core.OneKeyBind
import cc.polyfrost.oneconfig.config.data.Mod
import cc.polyfrost.oneconfig.config.data.ModType
import cc.polyfrost.oneconfig.config.migration.VigilanceMigrator
import cc.polyfrost.oneconfig.config.migration.VigilanceName
import cc.polyfrost.oneconfig.libs.universal.UKeyboard
import dev.dediamondpro.skyguide.gui.MapGui
import dev.dediamondpro.skyguide.handlers.AssetHandler
import dev.dediamondpro.skyguide.hud.MiniMap
import dev.dediamondpro.skyguide.utils.GuiUtils
import dev.dediamondpro.skyguide.utils.SBInfo
import dev.dediamondpro.skyguide.utils.TickDelay
import java.awt.Color

object Config : Config(
    Mod("SkyGuide", ModType.SKYBLOCK, VigilanceMigrator("config/skyguide/skyguide.toml")),
    "SkyGuide.json"
) {
    // General

    @Dropdown(
        name = "Texture Quality",
        description = "The quality of the textures.",
        options = ["low", "medium", "high"],
        category = "General"
    )
    @NonProfileSpecific
    var textureQuality = 1

    @Switch(
        name = "Download Assets at launch",
        description = "Download all assets at launch.",
        category = "General"
    )
    @VigilanceName( name = "Download at Launch", category = "General", subcategory = "")
    @NonProfileSpecific
    var downloadAtLaunch = false

    @Switch(
        name = "Keep Assets In Memory",
        description = "Keep all assets in memory.",
        category = "General"
    )
    @VigilanceName( name = "Keep In Memory", category = "General", subcategory = "")
    @NonProfileSpecific
    var keepAssetsLoaded = true

    @Switch(
        name = "Lazy Asset Loading",
        description = "Load assets as they are needed.",
        category = "General"
    )
    @VigilanceName( name = "Lazy Loading", category = "General", subcategory = "")
    @NonProfileSpecific
    var lazyLoading = true

    // Map

    @KeyBind(
        name = "Map Keybind",
        description = "The keybind to open the map.",
        category = "Map",
        size = 2
    )
    var mapKeyBind = OneKeyBind(UKeyboard.KEY_M)

    @Switch(
        name = "Show NPCs",
        description = "Show npcs on the map.",
        category = "Map",
        size = 2
    )
    var showNpcs = true


    @Switch(
        name = "Show MVP Warps",
        description = "Show MVP warps on the map.",
        category = "Map",
        size = 2
    )
    var showMVPWarps = true

    @cc.polyfrost.oneconfig.config.annotations.Color(
        allowAlpha = false,
        name = "Pin Color",
        description = "The color of the destination pin.",
        category = "Map",
        size = 2
    )
    var pinColor = OneColor(Color.RED)

    @Slider(
        name = "Default Scale",
        description = "The default scale of the map.",
        category = "Map",
        min = 0.25f, max = 5f
    )
    var defaultScale = 2f

    @Slider(
        name = "Player Pointer Size",
        description = "The size of the player pointer.",
        category = "Map",
        min = 7f, max = 49f
    )
    var mapPointerSize = 14f

    // Mini-Map

    @HUD(
        name = "Mini-Map",
        category = "Mini-Map"
    )
    var miniMap = MiniMap()

    // Integration

    @Switch(
        name = "Show Skytils Waypoints",
        description = "Show Skytils waypoints on the map.",
        category = "Integration",
        subcategory = "Skytils",
        size = 2
    )
    var skytilsWaypoints = true

    @Switch(
        name = "Show Disabled Skytils Waypoints",
        description = "Whether to show disabled Skytils waypoints on the map.",
        category = "Integration",
        subcategory = "Skytils",
        size = 2
    )
    var disabledSkytilsWaypoints = false

    // Hidden

    @NonProfileSpecific
    var downloadAssets = true

    @NonProfileSpecific
    var firstLaunchVersion = 0

    init {
        initialize()
        addListener("textureQuality") {
            TickDelay(1) {
                if (AssetHandler.downloadedAssets) {
                    AssetHandler.updateTextures()
                }
            }
        }
        addDependency("lazyLoading", "keepAssetsLoaded")
        registerKeyBind(mapKeyBind) {
            if (SBInfo.inSkyblock && enabled) GuiUtils.displayScreen(MapGui())
        }
    }
}
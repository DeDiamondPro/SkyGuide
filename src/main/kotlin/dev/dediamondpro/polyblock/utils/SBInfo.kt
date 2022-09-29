package dev.dediamondpro.polyblock.utils

import cc.polyfrost.oneconfig.events.event.LocrawEvent
import cc.polyfrost.oneconfig.libs.eventbus.Subscribe
import cc.polyfrost.oneconfig.renderer.RenderManager
import cc.polyfrost.oneconfig.utils.hypixel.LocrawInfo.GameType
import dev.dediamondpro.polyblock.config.BlockConfig
import dev.dediamondpro.polyblock.handlers.AssetHandler

class SBInfo {
    companion object {
        var inSkyblock = false
            private set
        var zone: String = "hub"
            private set
    }

    @Subscribe
    fun onLocraw(locrawEvent: LocrawEvent) {
        val info = locrawEvent.info
        if (info.gameType.equals(GameType.SKYBLOCK)) {
            inSkyblock = true
            if (zone != info.gameMode && !BlockConfig.keepAssetsLoaded) RenderManager.setupAndDraw {
                AssetHandler.unloadAssets(it)
            }
            if (!AssetHandler.downloadedAssets) AssetHandler.initialize()
            zone = info.gameMode
        } else {
            inSkyblock = false
        }
    }
}
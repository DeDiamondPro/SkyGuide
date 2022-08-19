package cc.polyfrost.polyblock.utils

import cc.polyfrost.oneconfig.events.event.LocrawEvent
import cc.polyfrost.oneconfig.libs.eventbus.Subscribe
import cc.polyfrost.oneconfig.renderer.RenderManager
import cc.polyfrost.oneconfig.utils.hypixel.LocrawInfo.GameType
import cc.polyfrost.polyblock.config.BlockConfig

class SBInfo {
    companion object {
        var inSkyblock = false
            private set
        var zone: String = "hub"
            private set
        var hasJoinedSb = false
            private set
    }

    @Subscribe
    fun onLocraw(locrawEvent: LocrawEvent) {
        val info = locrawEvent.info
        if (info.gameType.equals(GameType.SKYBLOCK)) {
            inSkyblock = true
            if (zone != info.gameMode && !BlockConfig.keepAssetsLoaded) RenderManager.setupAndDraw {
                AssetHandler.unloadAssets(
                    it
                )
            }
            if (!hasJoinedSb) {
                hasJoinedSb = true
                AssetHandler.initialize()
            }
            zone = info.gameMode
        } else {
            inSkyblock = false
        }
    }
}
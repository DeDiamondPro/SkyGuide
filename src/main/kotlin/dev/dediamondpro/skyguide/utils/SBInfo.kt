package dev.dediamondpro.skyguide.utils

import cc.polyfrost.oneconfig.events.event.LocrawEvent
import cc.polyfrost.oneconfig.libs.eventbus.Subscribe
import cc.polyfrost.oneconfig.utils.hypixel.LocrawUtil
import dev.dediamondpro.skyguide.handlers.AssetHandler

object SBInfo {
    val inSkyblock: Boolean
        get() = LocrawUtil.INSTANCE?.locrawInfo?.rawGameType == "SKYBLOCK"
    val zone: String
        get() = LocrawUtil.INSTANCE?.locrawInfo?.gameMode ?: "unknown"

    @Subscribe
    fun onLocrawEvent(e: LocrawEvent) {
        if (inSkyblock && !AssetHandler.downloadedAssets) AssetHandler.initialize()
    }
}
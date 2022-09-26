package dev.dediamondpro.polyblock.utils

import cc.polyfrost.oneconfig.libs.universal.wrappers.UPlayer

fun UPlayer.getOffsetX(): Float {
    return this.getPosX().toFloat()
}

fun UPlayer.getOffsetY(): Float {
    return this.getPosZ().toFloat()
}
package cc.polyfrost.polyblock.utils

import cc.polyfrost.oneconfig.libs.universal.wrappers.UPlayer

fun UPlayer.getX(): Float {
    return this.getPosX().toFloat()
}

fun UPlayer.getY(): Float {
    return this.getPosZ().toFloat()
}
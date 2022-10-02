package dev.dediamondpro.polyblock.utils

import gg.essential.universal.wrappers.UPlayer

fun UPlayer.getOffsetX(): Float {
    return this.getPosX().toFloat()
}

fun UPlayer.getOffsetY(): Float {
    return this.getPosZ().toFloat()
}
package dev.dediamondpro.skyguide.utils

import gg.essential.universal.wrappers.UPlayer

fun UPlayer.getOffsetX(partialTicks: Float): Float {
    return (getPrevPosX() + (getPosX() - getPrevPosX()) * partialTicks).toFloat()
}

fun UPlayer.getOffsetY(partialTicks: Float): Float {
    return (getPrevPosZ() + (getPosZ() - getPrevPosZ()) * partialTicks).toFloat()
}

fun UPlayer.getHeadRotation(partialTicks: Float): Float {
    return (getPlayer()!!.prevRotationYawHead + (getPlayer()!!.rotationYawHead - getPlayer()!!.prevRotationYawHead) * partialTicks) % 360
}
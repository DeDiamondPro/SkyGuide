package dev.dediamondpro.skyguide.utils

import kotlin.math.pow
import kotlin.math.roundToInt

fun Double.roundTo(digits: Int): Double {
    val factor = 10.0.pow(digits.toDouble())
    return (this * factor).roundToInt() / factor
}
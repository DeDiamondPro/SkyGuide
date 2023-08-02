package dev.dediamondpro.skyguide.map.poi

import dev.dediamondpro.skyguide.map.Island
import net.minecraft.item.ItemStack

interface Searchable {
    val searchString: String
    val searchDescription: String
    var island: Island?
    val x: Float
    val z: Float
    val scale: Float
    val skull: ItemStack
}
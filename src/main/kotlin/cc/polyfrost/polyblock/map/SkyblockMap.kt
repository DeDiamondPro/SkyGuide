package cc.polyfrost.polyblock.map

import cc.polyfrost.polyblock.utils.Island

object SkyblockMap {
    val islands = mutableMapOf(
        Pair("hub", Island("/assets/polyblock/hub.png", -298f, -258f, 201f, 241f)),
        Pair("combat_1", Island("/assets/polyblock/spiders_den.png", -408f, -408f, -106f, -106f)),
        Pair("foraging_1", Island("/assets/polyblock/park.png", -500f, -148f, -253f, 99f)),
        Pair("farming_1", Island("/assets/polyblock/farming_islands.png", 70f, -634f, 409f, -183f)), // 339x451
        Pair("crimson_isle", Island("/assets/polyblock/crimson_isle.png", -772f, -1180f, 51f, -357f)), // 823x823
        Pair("combat_3", Island("/assets/polyblock/end.png", -793f, -423f, -448f, -121f)), // 345x302
        Pair("mining_1", Island("/assets/polyblock/gold_mine.png", -90f, -401f, 45f, -266f)), // 135x135
        Pair("mining_2", Island("/assets/polyblock/deep_caverns.png", -86f, -68f, 80f, 98f, -12f, -579f)),  // 166x166,
    )
}
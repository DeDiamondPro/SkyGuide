package cc.polyfrost.polyblock.utils

import cc.polyfrost.oneconfig.config.annotations.Exclude
import cc.polyfrost.oneconfig.config.core.OneColor
import cc.polyfrost.oneconfig.config.gson.gsoninterface.GsonContext
import cc.polyfrost.oneconfig.config.gson.gsoninterface.JsonDeserialization
import cc.polyfrost.oneconfig.config.gson.gsoninterface.JsonDeserializes
import cc.polyfrost.polyblock.map.SkyblockMap
import com.google.gson.JsonElement
import net.minecraft.util.BlockPos
import java.lang.reflect.Type

data class Waypoint(
    val zone: String,
    val x: Float,
    val y: Float,
    var color: Int = OneColor.HSBAtoARGB((Math.random() * 360f).toFloat(), 100f, 100f, 255)
) {//: JsonDeserialization<Waypoint.WaypointDeserializer> {

    fun draw(partialTicks: Float) {
        if (!shouldDraw()) return
        RenderUtils.renderBeaconBeam(BlockPos(getOffsetX().toInt(), 0, getOffsetY().toInt()), color, partialTicks)
    }

    private fun getOffsetX(): Float {
        val offset = SkyblockMap.getCurrentIsland()?.xOffset ?: 0f
        return x - offset
    }

    private fun getOffsetY(): Float {
        val offset = SkyblockMap.getCurrentIsland()?.yOffset ?: 0f
        return y - offset
    }

    private fun shouldDraw(): Boolean {
        return SkyblockMap.isZoneInWorld(zone)
    }

    @Exclude
    class WaypointDeserializer : JsonDeserializes<Waypoint> {
        override fun fromJsonTree(json: JsonElement, type: Type, context: GsonContext<Waypoint>): Waypoint {
            println(json)
            val jsonObject = json.asJsonObject
            return Waypoint(
                jsonObject.get("zone").asString,
                jsonObject.get("x").asFloat,
                jsonObject.get("y").asFloat,
                jsonObject.get("color").asInt
            )
        }
    }
}
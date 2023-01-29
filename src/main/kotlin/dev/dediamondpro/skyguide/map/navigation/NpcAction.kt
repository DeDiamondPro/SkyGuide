package dev.dediamondpro.skyguide.map.navigation

import dev.dediamondpro.skyguide.map.poi.Npc
import dev.dediamondpro.skyguide.utils.RenderUtils
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumChatFormatting
import java.awt.Color
import javax.vecmath.Vector3f

class NpcAction(private val npc: Npc, private val destination: Destination) : NavigationAction {
    override fun drawAction(partialTicks: Float) {
        RenderUtils.renderBeaconBeam(
            BlockPos(npc.x.toDouble(), npc.y.toDouble(), npc.z.toDouble()),
            Color.RED.rgb,
            partialTicks
        )
        RenderUtils.renderWayPoint(
            mutableListOf(
                "Talk to ${EnumChatFormatting.BLUE}${npc.name}",
                "to reach ${EnumChatFormatting.BLUE}${destination.name}"
            ),
            Vector3f(npc.x, npc.y + 2, npc.z),
            partialTicks
        )
    }
}
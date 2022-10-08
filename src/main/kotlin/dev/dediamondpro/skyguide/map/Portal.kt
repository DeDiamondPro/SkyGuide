package dev.dediamondpro.skyguide.map

import kotlinx.serialization.Serializable

/**
 * @param destination The destination of the portal, null if the portal is command only
 * @param command The command to teleport to the portal, null if no command available
 * @param x The X coordinate of the portal
 * @param y The Y coordinate of the portal
 * @param z The Z coordinate of the portal
 */
@Serializable
data class Portal(val destination: String? = null, val command: String? = null, val x: Float, val y: Float, val z: Float)

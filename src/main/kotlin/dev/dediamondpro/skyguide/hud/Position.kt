package dev.dediamondpro.skyguide.hud

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import gg.essential.universal.UResolution.scaledWidth
import gg.essential.universal.UResolution.scaledHeight

class Position
/**
 * Position object used for huds
 *
 * @param x            The X coordinate
 * @param y            The Y coordinate
 * @param width        The width of the HUD
 * @param height       The height of the HUD
 * @param screenWidth  The width of the screen to initialize the position width
 * @param screenHeight The height of the screen to initialize the position width
 */(
    @Expose
    private var x: Float,
    @Expose
    private var y: Float,
    private var width: Float,
    private var height: Float,
    screenWidth: Float = scaledWidth.toFloat(),
    screenHeight: Float = scaledHeight.toFloat()
) {
    private var anchor: AnchorPosition? = null

    init {
        setSize(width, height)
        setPosition(x, y, screenWidth, screenHeight)
    }

    /**
     * Set the position
     *
     * @param x            The X coordinate
     * @param y            The Y coordinate
     * @param screenWidth  The screen width
     * @param screenHeight The screen height
     */
    fun setPosition(x: Float, y: Float, screenWidth: Float, screenHeight: Float) {
        val rightX = x + width
        val bottomY = y + height
        anchor =
            if (x <= screenWidth / 3f && y <= screenHeight / 3f) AnchorPosition.TOP_LEFT
            else if (rightX >= screenWidth / 3f * 2f && y <= screenHeight / 3f) AnchorPosition.TOP_RIGHT
            else if (x <= screenWidth / 3f && bottomY >= screenHeight / 3f * 2f) AnchorPosition.BOTTOM_LEFT
            else if (rightX >= screenWidth / 3f * 2f && bottomY >= screenHeight / 3f * 2f) AnchorPosition.BOTTOM_RIGHT
            else if (y <= screenHeight / 3f) AnchorPosition.TOP_CENTER
            else if (x <= screenWidth / 3f) AnchorPosition.MIDDLE_LEFT
            else if (rightX >= screenWidth / 3f * 2f) AnchorPosition.MIDDLE_RIGHT
            else if (bottomY >= screenHeight / 3f * 2f) AnchorPosition.BOTTOM_CENTER
            else AnchorPosition.MIDDLE_CENTER
        this.x = x - getAnchorX(screenWidth) + getAnchorX(width)
        this.y = y - getAnchorY(screenHeight) + getAnchorY(height)
    }

    /**
     * Set the position
     *
     * @param x The X coordinate
     * @param y The Y coordinate
     */
    fun setPosition(x: Float, y: Float) {
        setPosition(x, y, scaledWidth.toFloat(), scaledHeight.toFloat())
    }

    /**
     * Set the size of the position
     *
     * @param width  The width
     * @param height The height
     */
    fun setSize(width: Float, height: Float) {
        this.width = width
        this.height = height
    }

    /**
     * Update the position so the top left corner stays in the same spot
     *
     * @param width  The width
     * @param height The height
     */
    fun updateSizePosition(width: Float, height: Float) {
        val x = getX()
        val y = getY()
        setSize(width, height)
        setPosition(x, y)
    }

    /**
     * Get the X coordinate scaled to the size of the screen
     *
     * @param screenWidth The width of the screen
     * @return The X coordinate
     */
    fun getX(screenWidth: Float): Float {
        return x + getAnchorX(screenWidth) - getAnchorX(width)
    }

    /**
     * Get the X coordinate scaled to the size of the screen
     *
     * @return The X coordinate
     */
    fun getX(): Float {
        return getX(scaledWidth.toFloat())
    }

    /**
     * Get the Y coordinate scaled to the size of the screen
     *
     * @param screenHeight The height of the screen
     * @return The Y coordinate
     */
    fun getY(screenHeight: Float): Float {
        return y + getAnchorY(screenHeight) - getAnchorY(height)
    }

    /**
     * Get the Y coordinate scaled to the size of the screen
     *
     * @return The Y coordinate
     */
    fun getY(): Float {
        return getY(scaledHeight.toFloat())
    }

    /**
     * Get the X coordinate scaled to the size of the screen of the right corner
     *
     * @param screenWidth The width of the screen
     * @return The X coordinate of the right corner
     */
    fun getRightX(screenWidth: Float): Float {
        return getX(screenWidth) + width
    }

    /**
     * Get the X coordinate scaled to the size of the screen of the right corner
     *
     * @return The X coordinate of the right corner
     */
    fun getRightX(): Float {
        return getRightX(scaledWidth.toFloat())
    }

    /**
     * Get the Y coordinate scaled to the size of the screen of the bottom corner
     *
     * @param screenHeight The width of the screen
     * @return The Y coordinate of the bottom corner
     */
    fun getBottomY(screenHeight: Float): Float {
        return getY(screenHeight) + height
    }

    /**
     * Get the Y coordinate scaled to the size of the screen of the bottom corner
     *
     * @return The Y coordinate of the bottom corner
     */
    fun getBottomY(): Float {
        return getBottomY(scaledHeight.toFloat())
    }

    /**
     * Get the center X coordinate
     *
     * @param screenWidth The width of the screen
     * @return The center X coordinate
     */
    fun getCenterX(screenWidth: Float): Float {
        return getX(screenWidth) + width / 2f
    }

    /**
     * Get the center X coordinate
     *
     * @return The center X coordinate
     */
    fun getCenterX(): Float {
        return getCenterX(scaledWidth.toFloat())
    }

    /**
     * Get the center Y coordinate
     *
     * @param screenHeight The width of the screen
     * @return The center Y coordinate
     */
    fun getCenterY(screenHeight: Float): Float {
        return getY(screenHeight) + height / 2f
    }

    /**
     * Get the center Y coordinate
     *
     * @return The center Y coordinate
     */
    fun getCenterY(): Float {
        return getCenterY(scaledHeight.toFloat())
    }

    /**
     * @return The width of the position
     */
    fun getWidth(): Float {
        return width
    }

    /**
     * @return The height of the position
     */
    fun getHeight(): Float {
        return height
    }

    private fun getAnchorX(value: Float): Float {
        return value * anchor!!.x
    }

    private fun getAnchorY(value: Float): Float {
        return value * anchor!!.y
    }

    /**
     * Position of the anchors where the position is relative too
     */
    enum class AnchorPosition(val x: Float, val y: Float) {
        @SerializedName("0")
        TOP_LEFT(0f, 0f),

        @SerializedName("1")
        TOP_CENTER(0.5f, 0f),

        @SerializedName("2")
        TOP_RIGHT(1f, 0f),

        @SerializedName("3")
        MIDDLE_LEFT(0f, 0.5f),

        @SerializedName("4")
        MIDDLE_CENTER(0.5f, 0.5f),

        @SerializedName("5")
        MIDDLE_RIGHT(1f, 0.5f),

        @SerializedName("6")
        BOTTOM_LEFT(0f, 1f),

        @SerializedName("7")
        BOTTOM_CENTER(0.5f, 1f),

        @SerializedName("8")
        BOTTOM_RIGHT(1f, 1f);
    }
}
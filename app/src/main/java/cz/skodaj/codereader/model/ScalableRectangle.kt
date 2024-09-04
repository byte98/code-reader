package cz.skodaj.codereader.model

import android.graphics.Rect
import android.graphics.RectF
import android.util.Size
import kotlin.math.abs

/**
 * Class which represents rectangle with ability to scale.
 */
class ScalableRectangle{

    /**
     * Original size of surface on which rectangle lies on.
     */
    private val origin: Size?

    /**
     * Output built during modifying of original rectangle.
     */
    private val output: RectF

    /**
     * Creates new rectangle with ability to scale and move.
     * @param rect Rectangle which will be modified.
     * @param surface Original surface on which passed rectangle lies on.
     */
    public constructor(rect: Rect, surface: Size? = null){
        this.origin = surface
        this.output = RectF(
            rect.left.toFloat(),
            rect.top.toFloat(),
            rect.right.toFloat(),
            rect.bottom.toFloat()
        )
    }

    /**
     * Creates new rectangle with ability to scale and move.
     * @param original Original rectangle which data will be copied to the new one.
     */
    public constructor(original: ScalableRectangle){
        this.origin = original.origin
        this.output = original.output
    }

    /**
     * Scales rectangle on X axis.
     * @param factor Factor which will be used to scale rectangle on X axis.
     * @return Actually modified rectangle.
     */
    public fun scaleX(factor: Float): ScalableRectangle{
        val actualWidth: Float = this.width()
        val newWidth: Float = actualWidth * factor
        val deltaWidth: Float = abs(newWidth - actualWidth)
        val halfDelta: Float = deltaWidth / 2
        if (factor < 1){ // Shrink
            this.output.left = this.output.left + halfDelta
            this.output.right = this.output.right - halfDelta
        }
        else if (factor > 1){ // Enlarge
            this.output.left = this.output.left - halfDelta
            this.output.right = this.output.right + halfDelta
        }
        return this
    }

    /**
     * Scales rectangle on Y axis.
     * @param factor Factor which will be used to scale rectangle on Y axis.
     * @return Actually modified rectangle.
     */
    public fun scaleY(factor: Float): ScalableRectangle{
        val actualHeight: Float = this.height()
        val newHeight: Float = actualHeight * factor
        val deltaHeight: Float = abs(newHeight - actualHeight)
        val halfDelta: Float = deltaHeight / 2
        if (factor < 1){ // Shrink
            this.output.top = this.output.top + halfDelta
            this.output.bottom = this.output.bottom - halfDelta
        }
        else if (factor > 1){ // Enlarge
            this.output.top = this.output.top - halfDelta
            this.output.bottom = this.output.bottom + halfDelta
        }
        return this
    }

    /**
     * Scales rectangle.
     * @param factor Factor which will be used to scale rectangle.
     * @return Actually modified rectangle.
     */
    public fun scale(factor: Float): ScalableRectangle{
        this.scaleX(factor)
        this.scaleY(factor)
        return this
    }

    /**
     * Moves rectangle on X axis.
     * @param distance Distance of move; if less than zero - moves to the left, otherwise moves to the right.
     * @return Actually modified rectangle.
     */
    public fun moveX(distance: Float): ScalableRectangle{
        this.output.left = this.output.left + distance
        this.output.right = this.output.right + distance
        return this
    }

    /**
     * Moves rectangle on Y axis.
     * @param distance Distance of move; if less than zero - moves to the top, otherwise moves to the bottom.
     * @return Actually modified rectangle.
     */
    public fun moveY(distance: Float): ScalableRectangle{
        this.output.top = this.output.top + distance
        this.output.bottom = this.output.bottom + distance
        return this
    }

    /**
     * Scales rectangle without moving on X axis.
     * @param factor Factor used to scale on X axis.
     * @return Actually modified rectangle.
     */
    private fun scaleSimpleX(factor: Float): ScalableRectangle{
        this.output.left = this.output.left * factor
        this.output.right = this.output.right * factor
        return this
    }

    /**
     * Scales rectangle without moving on Y axis.
     * @param factor Factor used to scale on Y axis.
     * @return Actually modified rectangle.
     */
    private fun scaleSimpleY(factor: Float): ScalableRectangle{
        this.output.top = this.output.top * factor
        this.output.bottom = this.output.bottom * factor
        return this
    }


    /**
     * Gets width of actually modified rectangle.
     * @return Float representing width of actually modified rectangle.
     */
    public fun width(): Float{
        return abs(this.output.right - this.output.left)
    }

    /**
     * Gets height of actually modified rectangle.
     * @return Float representing height of actually modified rectangle.
     */
    public fun height(): Float{
        return abs(this.output.bottom - this.output.top)
    }

    /**
     * Creates new rectangle from actually modified one.
     * @param surface Size of output surface to which rectangle should be scaled.
     * @return Rectangle created from actually modified one.
     */
    public fun toRectangle(surface: Size?): RectF{
        var reti: ScalableRectangle = this
        if (surface != null && this.origin != null) {
            val scaleX = surface.width.toFloat() / this.origin.width.toFloat()
            val scaleY = surface.height.toFloat() / this.origin.height.toFloat()
            reti = ScalableRectangle(this).scaleSimpleX(scaleX).scaleSimpleY(scaleY).correctFit()
        }
        return reti.output
    }

    /**
     * Corrects fit to the size of output surface.
     * Why this function needs to be called? I have really no idea, but when deleted,
     * requested rectangle is slightly misplaced ¯\_(ツ)_/¯
     *
     * @return Actually modified rectangle.
     */
    private fun correctFit(): ScalableRectangle{
        return this
    }


}

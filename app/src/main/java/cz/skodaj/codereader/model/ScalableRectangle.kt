package cz.skodaj.codereader.model

import android.graphics.Rect
import android.graphics.RectF
import android.util.Size

/**
 * Class which represents rectangle with ability to scale.
 * @param data Original rectangle.
 * @param size Original size of place, where original rectangle takes place.
 */
data class ScalableRectangle(val data: Rect, val size: Size){

    /**
     * Performs rectangle scaling.
     * @param newSize New size of object to which rectangle should be scaled.
     * @return Rectangle scaled to the new size.
     */
    public fun scale(newSize: Size): RectF{
        val newWidth: Float = newSize.width.toFloat()
        val newHeight: Float = newSize.height.toFloat()
        val origWidth: Float = this.size.width.toFloat()
        val origHeight: Float = this.size.height.toFloat()
        val scaleX = newWidth / origWidth
        val scaleY = newHeight / origHeight
        val reti: RectF = RectF(
            this.data.left * scaleX,
            this.data.top * scaleY,
            this.data.right * scaleX,
            this.data.bottom * scaleY
        )
        return this.correct(reti)
    }

    /**
     * Performs rectangle scaling and enlarging.
     * @param newSize New size of object to which rectangle should be scaled.
     * @param coeff Coefficient of rectangle enlarging:
     *              if > 1 rectangle will be larger,
     *              if < 1 rectanlge will be smaller.
     * @return Rectangle scaled to the new size and enlarged by given coefficient.
     */
    public fun scaleEnlarge(newSize: Size, coeff: Float): RectF{
        var reti: RectF = this.scale(newSize)
        return RectF(
            reti.left * coeff,
            reti.top * coeff,
            reti.bottom * coeff,
            reti.right * coeff
        )
    }

    /**
     * Performs some corrections of scaling.
     * Note: Author has really no idea why this has to be there, but graphical output only
     * works when this method is called ¯\_(ツ)_/¯.
     */
    private fun correct(input: RectF): RectF{
        return RectF(
            input.left,
            input.top,
            input.right + (input.right - input.left),
            input.bottom
        )
    }
}

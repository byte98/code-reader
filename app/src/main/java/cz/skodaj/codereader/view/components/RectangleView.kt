package cz.skodaj.codereader.view.components

import android.graphics.Rect
import android.graphics.RectF
import android.view.View
import android.widget.ImageView
import androidx.core.graphics.toRectF

/**
 * Interface defining contract for every view with ability to display rectangle.
 */
interface RectangleView {

    /**
     * Sets rectangle which will be drawn.
     * @param rect Rectangle which will be drawn.
     */
    public abstract fun setRectangle(rect: RectF?)

    /**
     * Sets rectangle which will be drawn.
     * @param rect Rectangle which will be drawn.
     */
    public fun setRectangle(rect: Rect?){
        this.setRectangle(rect?.toRectF())
    }
}

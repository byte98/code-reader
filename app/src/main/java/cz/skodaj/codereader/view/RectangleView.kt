package cz.skodaj.codereader.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.solver.widgets.Rectangle
import androidx.core.content.ContextCompat
import cz.skodaj.codereader.R

/**
 * View which just draws rectangle.
 */
class RectangleView @JvmOverloads constructor(

    /**
     * Context of the application.
     */
    context: Context,

    /**
     * Set of attributes of view.
     */
    attrs: AttributeSet? = null,

    /**
     * Default style of view.
     */
    defStyleAttr: Int = 0
    )
    : View(context, attrs, defStyleAttr) {

    /**
     * Paint which performs rectangle drawing.
     */
    private val paint: Paint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.colorAccent)
        style = Paint.Style.STROKE
        strokeWidth = 8f
    }

    /**
     * Actually drawn rectangle.
     */
    private var rect: RectF? = null

    /**
     * Sets rectangle which will be drawn.
     * @param rectangle Rectangle which will be drawn.
     */
    public fun setRectangle(rectangle: RectF?){
        this.rect = rectangle
        this.invalidate()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        this.rect?.let {
            canvas?.drawRect(it, this.paint)
        }
    }
}
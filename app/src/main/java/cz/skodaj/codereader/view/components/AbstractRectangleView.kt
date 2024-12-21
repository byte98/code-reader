package cz.skodaj.codereader.view.components

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup

/**
 * Class which abstracts all other views with ability to draw rectangles.
 */
abstract class AbstractRectangleView<T: View>

/**
 * Creates new instance of view.
 * @param context Actual context of the application.
 * @param attrs Attributes of view.
 * @param defStyleAttr Attribute which holds default style.
 */
@JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
    )
: ViewGroup(context, attrs, defStyleAttr), RectangleView{

    /**
     * Rectangle which will be drawn inside view.
     */
    private var rect: RectF? = null

    /**
     * Scale factor used to scale rectangle.
     */
    private var scale: Float = 1f

    public override final fun setRectangle(rect: RectF?) {
        this.rect = rect
        this.invalidate()
    }

    /**
     * Sets scaling factor of rectangle.
     * @param scale New scaling factor of rectangle.
     */
    protected final fun setScale(scale: Float = 1f){
        this.scale = scale
    }

    /**
     * Function which will be called before drawing of rectangle.
     * @param canvas Canvas used for drawing.
     */
    public open fun beforeDraw(canvas: Canvas){
        // NOP
    }

    /**
     * Function which will be called after drawing of rectangle.
     * @param canvas Canvas used for drawing.
     */
    public open fun afterDraw(canvas: Canvas){
        // NOP
    }

    public final override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let { safeCanvas ->
            this.beforeDraw(safeCanvas)
            rect?.let { rectangle ->

                // Create a new rectangle that will be drawn
                val drawingRect = RectF()

                // Intersect the original rectangle with view bounds
                drawingRect.left = (rectangle.left * this.scale).coerceIn(0f, width.toFloat())
                drawingRect.top = (rectangle.top * this.scale).coerceIn(0f, height.toFloat())
                drawingRect.right = (rectangle.right * this.scale).coerceIn(0f, width.toFloat())
                drawingRect.bottom = (rectangle.bottom * this.scale).coerceIn(0f, height.toFloat())

                // Get colorAccent from theme
                val accentColor = context.obtainStyledAttributes(
                    intArrayOf(android.R.attr.colorAccent)
                ).use {
                    it.getColor(0, 0)
                }

                // Create and configure the paint
                val paint = Paint().apply {
                    style = Paint.Style.STROKE
                    color = accentColor
                    strokeWidth = 2f
                    isAntiAlias = true
                }

                // Draw the trimmed rectangle
                safeCanvas.drawRect(drawingRect, paint)
            }
            this.afterDraw(safeCanvas)
        }
    }

    /**
     * Gets actual scaling factor of rectangle.
     * @return Float representing actual scaling factor of rectangle.
     */
    protected final fun getScale(): Float{
        return this.scale
    }

    override fun onLayout(p0: Boolean, p1: Int, p2: Int, p3: Int, p4: Int) {
        // NOP
    }
}
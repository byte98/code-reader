package cz.skodaj.codereader.view.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import cz.skodaj.codereader.R
import kotlin.math.min

/**
 * View which just draws rectangle.
 */
class RectangleImageView @JvmOverloads constructor(

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
     * Bitmap to be drawn as the image.
     */
    private var image: Bitmap? = null

    /**
     * Scale factor for upscaling the view.
     */
    private var scaleFactor: Float = 1f

    /**
     * Sets the image to be displayed.
     * @param bitmap Bitmap to be displayed. Passing null will clear the image.
     */
    fun setImage(bitmap: Bitmap?) {
        this.image = bitmap
        if (bitmap == null){
            Log.d(this::class.qualifiedName, "Image unset")
        }
        else{
            Log.d(this::class.qualifiedName, "Image set; size (h×w):${bitmap.height}×${bitmap.width}")
        }
        this.requestLayout() // Force resize
        this.invalidate() // Force redraw
    }

    /**
     * Sets the rectangle to be drawn on top of the image.
     * @param rectangle Rectangle to overlay. Passing null will clear the rectangle.
     */
    fun setRectangle(rectangle: RectF?) {
        this.rect = rectangle
        if (rect == null){
            Log.d(this::class.qualifiedName, "Rectangle unset")
        }
        else{
            Log.d(this::class.qualifiedName, "Rectangle set (${rectangle})")
        }
        this.invalidate() // Force redraw
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.let {
            if (image != null) {
                // Draw the image scaled to the view's dimensions
                val scaledImageRect = RectF(0f, 0f, width.toFloat(), height.toFloat())
                it.drawBitmap(image!!, null, scaledImageRect, null)
            } else {
                // Fill with the background color
                it.drawColor(ContextCompat.getColor(context, android.R.color.background_light))
            }

            // Draw the rectangle if it's set
            rect?.let { rectangle ->
                // Scale the rectangle to match the scaled image size
                val scaledRect = RectF(
                    rectangle.left * scaleFactor,
                    rectangle.top * scaleFactor,
                    rectangle.right * scaleFactor,
                    rectangle.bottom * scaleFactor
                )
                it.drawRect(scaledRect, paint)
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        image?.let {
            // Get the image dimensions
            val imageWidth = it.width
            val imageHeight = it.height

            // Get the parent's size constraints
            val parentWidth = MeasureSpec.getSize(widthMeasureSpec)
            val parentHeight = MeasureSpec.getSize(heightMeasureSpec)

            // Calculate the scale factor to fit the image proportionally inside the parent
            scaleFactor = min(parentWidth.toFloat() / imageWidth, parentHeight.toFloat() / imageHeight)

            // Set the new dimensions for this view
            val scaledWidth = (imageWidth * scaleFactor).toInt()
            val scaledHeight = (imageHeight * scaleFactor).toInt()
            setMeasuredDimension(scaledWidth, scaledHeight)
        } ?: run {
            // Default behavior if no image is set
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }
}
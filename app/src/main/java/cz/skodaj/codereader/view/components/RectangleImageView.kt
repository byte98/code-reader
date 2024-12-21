package cz.skodaj.codereader.view.components

import android.content.Context
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.ImageView
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
    : AbstractRectangleView<ImageView>(context, attrs, defStyleAttr) {


    /**
     * Bitmap to be drawn as the image.
     */
    private var image: Bitmap? = null

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

    override fun beforeDraw(canvas: Canvas){
        val backgroundAttr = context.obtainStyledAttributes(
            intArrayOf(android.R.attr.background)
        ).use {
            it.getDrawable(0)
        }
        if (this.image != null){
            val scaledImageRect = RectF(0f, 0f, width.toFloat(), height.toFloat())
            canvas.drawBitmap(image!!, null, scaledImageRect, null)
        }
        else{
            canvas.drawColor((backgroundAttr as? ColorDrawable)?.color ?: Color.BLACK)
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
            this.setScale(min(parentWidth.toFloat() / imageWidth, parentHeight.toFloat() / imageHeight))

            // Set the new dimensions for this view
            val scaledWidth = (imageWidth * this.getScale()).toInt()
            val scaledHeight = (imageHeight * this.getScale()).toInt()
            setMeasuredDimension(scaledWidth, scaledHeight)
        } ?: run {
            // Default behavior if no image is set
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }
}
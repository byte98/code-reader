package cz.skodaj.codereader.view.components


import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import androidx.camera.view.PreviewView

/**
 * View which draws rectangle over preview view.
 */
/**
 * View which just draws rectangle.
 */
class RectanglePreviewView @JvmOverloads constructor(

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
     * Preview view used to display camera input.
     */
    private val previewView: PreviewView


    init {
        // Initialize the PreviewView and add it to the layout
        previewView = PreviewView(context, attrs).apply {
            layoutParams = LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT
            )
        }
        addView(previewView)
    }



    /**
     * Exposes the underlying PreviewView.
     * @return The underlying PreviewView.
     */
    public fun getPreviewView(): PreviewView = previewView
}

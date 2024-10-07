package cz.skodaj.codereader.utils

import android.graphics.*
import android.media.Image
import android.util.Base64
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.nio.ByteBuffer

/**
 * Object which contains some utility functions to work with images.
 */
object ImageUtils {

    /**
     * Factor used to determine background colors of text image.
     */
    private const val TextImage_BackgroundFactor: Float = 0.3F

    /**
     * Encodes image to Base64 string.
     * @param image Image which will be encoded.
     * @return String containing encoded image,
     *         or NULL if something bad happened.
     */
    public fun toBase64(image: Image): String?{
        var reti: String? = null
        if (image.format == ImageFormat.JPEG || image.format == ImageFormat.HEIC){
            val buffer: ByteBuffer = image.planes[0].buffer
            val bytes: ByteArray = ByteArray(buffer.remaining())
            buffer.get(bytes)
            reti = Base64.encodeToString(bytes, Base64.DEFAULT)
        }
        else{
            throw UnsupportedOperationException("Unsupported image format!")
        }
        return reti
    }

    /**
     * Encodes bitmap to Base64 string.
     * @param bitmap Bitmap which will be encoded.
     * @return String containing encoded bitmap,
     *         or NULL if something gone wrong.
     */
    public fun toBase64(bitmap: Bitmap): String?{
        var reti: String? = null
        val bytes: Int = bitmap.byteCount
        val buffer: ByteBuffer = ByteBuffer.allocate(bytes)
        bitmap.copyPixelsToBuffer(buffer)
        val array: ByteArray = buffer.array()
        reti = Base64.encodeToString(array, Base64.DEFAULT)
        return reti
    }

    /**
     * Converts image into bitmap.
     * @param image Image which will be converted.
     * @return Bitmap with data from the image,
     *         or NULL if something gone wrong.
     */
    public fun toBitmap(image: Image): Bitmap?{
        var reti: Bitmap? = null
        val buffer: ByteBuffer = image.planes[0].buffer
        val bytes: ByteArray = ByteArray(buffer.remaining())
        reti = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        return reti
    }

    /**
     * Decodes image from Base64 string.
     * @param base64 String containing Base64 encoded image.
     * @return Bitmap of image gathered from Base64 string,
     *         or NULL if image cannot be gathered.
     */
    public fun toImage(base64: String): Bitmap?{
        var reti: Bitmap? = null
        if (base64.length > 1) {
            val decoded: ByteArray = Base64.decode(base64, Base64.DEFAULT)
            reti = BitmapFactory.decodeByteArray(decoded, 0, decoded.size)
        }
        return reti
    }

    /**
     * Creates image with text.
     * @param width: Width of image (in pixels).
     * @param height: Height of image (in pixels).
     * @param background: Background color of image.
     * @param foreground: Color of text.
     * @param text: Text which will appear in the image.
     */
    public fun textImage(width: Int, height: Int, background: Color, foreground: Color, text: String): Bitmap{
        val reti: Bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas: Canvas = Canvas(reti)

        // Draw background
        val bg1: Int = ImageUtils.lightenColor(background, ImageUtils.TextImage_BackgroundFactor).toArgb()
        val bg2: Int = ImageUtils.darkenColor(background, ImageUtils.TextImage_BackgroundFactor).toArgb()
        val gradient: LinearGradient = LinearGradient(
            0f, 0f, width.toFloat(), height.toFloat(),
            bg1, bg2,
            Shader.TileMode.CLAMP
        )
        val backgroundPaint: Paint = Paint().apply {
            shader = gradient
        }
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), backgroundPaint)

        // Draw text
        val fg: Int = foreground.toArgb()
        val paint: Paint = Paint().apply {
            color = fg
            textSize = (minOf(width, height) / 2).toFloat()
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
            style = Paint.Style.FILL
        }
        val textBounds: Rect = Rect()
        paint.getTextBounds(text, 0, text.length, textBounds)
        val x: Float = width / 2f
        val y: Float = (height / 2f) - textBounds.exactCenterY()
        canvas.drawText(text, x, y, paint)
        return reti
    }

    /**
     * Creates darker color.
     * @param color Color which darker variant will be returned.
     * @param factor Factor of darkening (0 ≤ factor ≤ 1).
     * @return Darker variant of passed color.
     */
    public fun darkenColor(color: Color, factor: Float): Color{
        return Color.valueOf(
            maxOf(color.red() * (1 - factor), 0.0f),
            maxOf(color.green() * (1 - factor), 0.0f),
            maxOf(color.blue() * (1 - factor), 0.0f)
        )
    }

    /**
     * Creates lighter color.
     * @param color Color which lighter variant will be returned.
     * @param factor Factor of lightening (0 ≤ factor ≤ 1).
     * @return Lighter variant of passed color.
     */
    public fun lightenColor(color: Color, factor: Float): Color{
        return Color.valueOf(
            minOf(color.red() * (1 + factor), 1.0f),
            minOf(color.green() * (1 + factor), 1.0f),
            minOf(color.blue() * (1 + factor), 1.0f)
        )
    }
}
package cz.skodaj.codereader.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
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
        val decoded: ByteArray = Base64.decode(base64, Base64.DEFAULT)
        reti = BitmapFactory.decodeByteArray(decoded, 0, decoded.size)
        return reti
    }
}
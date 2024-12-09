    package cz.skodaj.codereader.utils

    import android.content.Context
    import android.content.res.TypedArray
    import android.graphics.*
    import android.icu.text.SimpleDateFormat
    import android.media.Image
    import android.util.Base64
    import android.util.Log
    import android.util.TypedValue
    import androidx.core.content.ContextCompat
    import cz.skodaj.codereader.R
    import cz.skodaj.codereader.model.CodeInfo
    import java.io.ByteArrayOutputStream
    import java.io.File
    import java.io.FileOutputStream
    import java.io.OutputStream
    import java.nio.ByteBuffer
    import java.security.MessageDigest
    import java.util.*

    /**
     * Object which contains some utility functions to work with images.
     */
    object ImageUtils {

        /**
         * Factor used to determine background colors of text image.
         */
        private const val TextImageBackgroundFactor: Float = 0.25F

        /**
         * Default width of unknown images (in pixels).
         */
        private val UnknownImageWidth: Int = 512

        /**
         * Default height of unknown images (in pixels).
         */
        private val UnknownImageHeight: Int = 512

        /**
         * Default text in the unknown images.
         */
        private val UnknownImageText: String = "?"


        /**
         * Encodes bitmap to Base64 string.
         * @param bitmap Bitmap which will be encoded.
         * @param format Compression format (e.g., Bitmap.CompressFormat.PNG or Bitmap.CompressFormat.JPEG).
         * @param quality Quality of compression (0-100).
         * @return String containing encoded bitmap,
         *         or NULL if something gone wrong.
         */
        public fun toBase64(
            bitmap: Bitmap,
            format: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG,
            quality: Int = 100): String?{
            /*
            var reti: String? = null
            val bytes: Int = bitmap.byteCount
            val buffer: ByteBuffer = ByteBuffer.allocate(bytes)
            bitmap.copyPixelsToBuffer(buffer)
            val array: ByteArray = buffer.array()
            reti = Base64.encodeToString(array, Base64.DEFAULT)
            Log.d(this::class.qualifiedName, "Created base64 string from bitmap (output size: ${reti.length})")
            return reti
             */
            var reti: String? = null
            try{
                val outputStream: ByteArrayOutputStream = ByteArrayOutputStream()
                bitmap.compress(format, quality, outputStream)
                val byteArray: ByteArray = outputStream.toByteArray()
                reti = Base64.encodeToString(byteArray, Base64.DEFAULT)
                Log.d(this::class.qualifiedName, "Successfully converted bitmap to base64 string (output size: ${reti.length})")
            }
            catch (e: Exception){
                Log.e(this::class.qualifiedName, "Conversion bitmap to base64 string failed: ${e.message}!")
            }
            return reti
        }

        /**
         * Decodes image from Base64 string.
         * @param base64 String containing Base64 encoded image.
         * @return Bitmap of image gathered from Base64 string,
         *         or NULL if image cannot be gathered.
         */
        public fun toBitmap(base64: String): Bitmap?{
            var reti: Bitmap? = null
            try {
                if (base64.length > 1) {
                    val decodedBytes: ByteArray = Base64.decode(base64, Base64.DEFAULT)
                    reti = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                    Log.d(this::class.qualifiedName,"Successfully decoded base64 string to bitmap (input size: ${base64.length}; output size (h×w): ${reti.height}×${reti.width})")
                }
                else{
                    Log.e(this::class.qualifiedName, "Failed to decode base64 string to bitmap: input is empty!")
                }
            }
            catch(e: Exception){
                Log.e(this::class.qualifiedName, "Failed to decode base64 string to bitmap:: ${e.message}!")
            }
            return reti
        }

        /**
         * Converts image into bitmap.
         * @param image Image which will be converted.
         * @return Bitmap with data from the image,
         *         or NULL if something gone wrong.
         */
        fun toBitmap(image: Image): Bitmap? {
            var reti: Bitmap? = null
            if (image.format != ImageFormat.YUV_420_888) {
                Log.e(this::class.qualifiedName, "Unsupported image format: ${image.format}")
                reti = null
            }
            else {
                val planes = image.planes
                val yPlane = planes[0]
                val uPlane = planes[1]
                val vPlane = planes[2]

                val ySize = yPlane.buffer.remaining()
                val uSize = uPlane.buffer.remaining()
                val vSize = vPlane.buffer.remaining()

                val nv21 = ByteArray(ySize + uSize + vSize)

                // Copy Y plane
                yPlane.buffer.get(nv21, 0, ySize)

                val width = image.width
                val height = image.height

                // Adjust U and V planes to NV21 format (V first, then U)
                val uvHeight = height / 2
                val uvStride = uPlane.rowStride

                val uBuffer = uPlane.buffer
                val vBuffer = vPlane.buffer

                val uvPixelStride = uPlane.pixelStride
                var uvIndex = ySize
                for (row in 0 until uvHeight) {
                    val uRowStart = row * uvStride
                    val vRowStart = row * uvStride

                    for (col in 0 until width / 2) {
                        nv21[uvIndex++] = vBuffer[vRowStart + col * uvPixelStride]
                        nv21[uvIndex++] = uBuffer[uRowStart + col * uvPixelStride]
                    }
                }

                // Convert NV21 to Bitmap
                val yuvImage = YuvImage(nv21, ImageFormat.NV21, width, height, null)
                val outputStream = ByteArrayOutputStream()
                yuvImage.compressToJpeg(Rect(0, 0, width, height), 100, outputStream)
                val jpegByteArray = outputStream.toByteArray()

                reti = BitmapFactory.decodeByteArray(jpegByteArray, 0, jpegByteArray.size).also {
                    image.close() // Free resources
                }
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

            try {
                // Draw background
                val bg1: Int = ImageUtils.lightenColor(background, ImageUtils.TextImageBackgroundFactor).toArgb()
                val bg2: Int = ImageUtils.darkenColor(background, ImageUtils.TextImageBackgroundFactor).toArgb()
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
            } catch (e: Exception) {
                Log.e("ImageUtils", "Error creating text image", e)
            }

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
                maxOf(color.blue() * (1 - factor), 0.0f),
                color.alpha()  // Preserve alpha
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
                minOf(color.blue() * (1 + factor), 1.0f),
                color.alpha()  // Preserve alpha
            )
        }

        /**
         * Creates image which represents unknown image.
         * @param context Actual context of the application.
         * @return Bitmap representing unknown image.
         */
        public fun getUnknownImage(context: Context): Bitmap{
            var foregroundColor = Color.WHITE
            var backgroundColor = Color.BLACK

            try {
                val typedValue: TypedValue = TypedValue()
                if (context.theme.resolveAttribute(android.R.attr.textColorPrimary, typedValue, true)) {
                    foregroundColor = ContextCompat.getColor(context, typedValue.resourceId)
                }
                if (context.theme.resolveAttribute(android.R.attr.windowBackground, typedValue, true)) {
                    backgroundColor = ContextCompat.getColor(context, typedValue.resourceId)
                }
            } catch (e: Exception) {
                Log.e("ImageUtils", "Error retrieving theme colors", e)
            }
            return textImage(
                UnknownImageWidth,
                UnknownImageHeight,
                Color.valueOf(backgroundColor),
                Color.valueOf(foregroundColor),
                UnknownImageText
            )
        }

        /**
         * Generates hash of bitmap data.
         * @param hashAlgorithm Algorithm used to generate hash.
         * @return String representing hash of bitmap data.
         */
        private fun Bitmap.generateHash(hashAlgorithm: String = "SHA-256"): String {
            val outputStream = ByteArrayOutputStream()
            this.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            val bitmapBytes = outputStream.toByteArray()
            val digest = MessageDigest.getInstance(hashAlgorithm)
            val hashBytes = digest.digest(bitmapBytes)
            return hashBytes.joinToString("") { "%02x".format(it) }
        }

        /**
         * Checks, whether given image has same content as the unknown image.
         * @param image Image which will be checked.
         * @param context Actual context of the application.
         * @return TRUE if image has same content as the unknown image,
         *         FALSE otherwise.
         */
        public fun isUnknown(image: Image?, context: Context): Boolean{
            var reti: Boolean = false
            if (image != null){
                reti = ImageUtils.isUnknown(ImageUtils.toBitmap(image), context)
            }
            return reti
        }

        /**
         * Checks, whether given image has same content as the unknown image.
         * @param image Image which will be checked.
         * @param context Actual context of the application.
         * @return TRUE if image has same content as the unknown image,
         *         FALSE otherwise.
         */
        public fun isUnknown(image: Bitmap?, context: Context): Boolean{
            var reti: Boolean = false
            if (image != null){
                val unknownHash: String = ImageUtils.getUnknownImage(context).generateHash()
                val givenHash:   String = image.generateHash()
                reti = (unknownHash == givenHash)
            }
            return reti
        }

        /**
         * Converts a Bitmap into a binary array.
         * @param bitmap Bitmap to be converted.
         * @param format The desired compression format (e.g., Bitmap.CompressFormat.PNG or Bitmap.CompressFormat.JPEG).
         * @param quality Quality of compression (0-100, where 100 is maximum quality).
         * @return A ByteArray containing the compressed Bitmap data.
         */
        fun bitmapToByteArray(bitmap: Bitmap, format: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG, quality: Int = 100): ByteArray {
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(format, quality, outputStream)
            return outputStream.toByteArray()
        }

        /**
         * Writes bitmap to PNG file.
         * @param bitmap Bitmap which will be written to the file.
         * @param context Actual context of the application.
         * @param name Name of new file.
         * @param quality Quality of compression (0-100, where 100 is maximum quality).
         */
        fun writeToPNG(bitmap: Bitmap, context: Context, name: String = "CODE__${
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(
                Date()
            )}.PNG", quality: Int = 100){
            val file: File = File(context.filesDir, name)
            file.writeBytes(ImageUtils.bitmapToByteArray(bitmap, Bitmap.CompressFormat.PNG, quality))
            Log.d(this::class.qualifiedName, "Bitmap successfully written to PNG file '${name}'")
        }

        /**
         * Writes bitmap to JPG file.
         * @param bitmap Bitmap which will be written to the file.
         * @param context Actual context of the application.
         * @param name Name of new file.
         * @param quality Quality of compression (0-100, where 100 is maximum quality).
         */
        fun writeToJPG(bitmap: Bitmap, context: Context, name: String = "CODE__${
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(
                Date()
            )}.JPEG", quality: Int = 100){
            val file: File = File(context.filesDir, name)
            file.writeBytes(ImageUtils.bitmapToByteArray(bitmap, Bitmap.CompressFormat.JPEG, quality))
            Log.d(this::class.qualifiedName, "Bitmap successfully written to JPEG file '${name}'")
        }

        /**
         * Rotates bitmap.
         * @param bitmap Bitmap which will be rotated.
         * @param degrees How much will be bitmap rotated.
         * @return Rotated bitmap.
         */
        fun rotate(bitmap: Bitmap, degrees: Float): Bitmap {
            val matrix = android.graphics.Matrix().apply {
                postRotate(degrees)
            }
            return Bitmap.createBitmap(
                bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true
            )
        }
    }
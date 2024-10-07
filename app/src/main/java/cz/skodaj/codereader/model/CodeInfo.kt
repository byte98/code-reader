package cz.skodaj.codereader.model

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Rect
import android.media.Image
import androidx.core.graphics.toRect
import cz.skodaj.codereader.utils.DataSize
import cz.skodaj.codereader.utils.ImageUtils
import cz.skodaj.codereader.utils.MapUtils
import java.time.LocalDateTime


/**
 * Class which holds some basic information about code.
 */
open class CodeInfo {

    /**
     * Date and time of creation.
     */
    private val creationDate: LocalDateTime

    /**
     * Type of code.
     */
    private val codeType: CodeType

    /**
     * Image containing code.
     */
    private val image: Bitmap

    /**
     * Position of the code in the image.
     */
    private val position: Rect

    /**
     * Type of data stored in code.
     */
    private val dataType: DataType

    /**
     * Data stored in code.
     */
    private val data: String

    /**
     * Fields of data stored in code (if available).
     */
    private val dataFields: Map<String, String>

    /**
     * Size of data stored in code.
     */
    private val size: DataSize

    /**
     * Creates new holder of basic information about code.
     * @param creationDate Date and time of creation.
     * @param codeType Type of code.
     * @param image Image containing code.
     * @param position Position of the code in the image.
     * @param dataType Type of data stored in code.
     * @param data Data stored in code.
     * @param size Size of data stored in code.
     * @param dataFields Fields of data stored in code (if available).
     */
    public constructor(
        creationDate: LocalDateTime,
        codeType: CodeType,
        image: Bitmap,
        position: Rect,
        dataType: DataType,
        data: String,
        size: Double,
        dataFields: Map<String, String> = emptyMap()
    ){
        this.creationDate = creationDate
        this.codeType = codeType
        this.image = image
        this.position = position
        this.dataType = dataType
        this.data = data
        this.dataFields = dataFields
        this.size = DataSize(size)
    }

    /**
     * Gets creation date and time of code.
     * @return Date and time of creation of code.
     */
    public fun getCreationDate(): LocalDateTime{
        return this.creationDate
    }

    /**
     * Gets type of code.
     * @return Type of code.
     */
    public fun getCodeType(): CodeType{
        return this.codeType
    }

    /**
     * Gets image in which is code located.
     * @return Image containing code.
     */
    public fun getImage(): Bitmap{
        return this.image
    }

    /**
     * Gets position of the code in the image.
     * @return Rectangle defining position of code in the image.
     */
    public fun getPosition(): Rect{
        return this.position
    }

    /**
     * Gets data type of data in code.
     * @return Data type of data in code.
     */
    public fun getDataType(): DataType{
        return this.dataType
    }

    /**
     * Gets raw data stored in code.
     * @return String containing raw data stored in code.
     */
    public fun getData(): String{
        return this.data
    }

    /**
     * Checks, whether code has any defined data fields.
     * @return TRUE if code has defined any data fields,
     *         FALSE otherwise.
     */
    public fun hasDataFields(): Boolean{
        return this.dataFields.isNotEmpty()
    }

    /**
     * Gets names of all data fields.
     * @return Array with names of all data fields.
     */
    public fun getDataFields(): Array<String>{
        return this.dataFields.keys.toTypedArray()
    }

    /**
     * Gets value of data field.
     * @param name Name of data field.
     * @return Value of data field of given name,
     *         or NULL if there is no such data field.
     */
    public fun getDataField(name: String): String?{
        var reti: String? = null
        if (this.dataFields.containsKey(name)){
            reti = this.dataFields.get(name)
        }
        return reti
    }

    /**
     * Gets size of data in bytes.
     * @return Number representing size of data stored in code.
     */
    public fun getSize(): Double{
        return this.size.getSize()
    }

    /**
     * Gets size of data in human readable text.
     * @return String representing size of data stored in code.
     */
    public fun getSizeString(): String{
        return this.size.getCombined()
    }

    companion object{

        /**
         * Default width of unknown images (in pixels).
         */
        private val UnknownImageWidth: Int = 400

        /**
         * Default height of unknown images (in pixels).
         */
        private val UnknownImageHeight: Int = 400

        /**
         * Default color of unknown images background.
         */
        private val UnknownImageBackground: Color = Color.valueOf(Color.BLACK)

        /**
         * Default color of unknown images foreground.
         */
        private val UnknownImageForeground: Color = Color.valueOf(Color.WHITE)

        /**
         * Default text in the unknown images.
         */
        private val UnknownImageText: String = "?"

        /**
         * Unknown image.
         */
        private val UnknownImage: Bitmap = ImageUtils.textImage(
            CodeInfo.UnknownImageWidth,
            CodeInfo.UnknownImageHeight,
            CodeInfo.UnknownImageBackground,
            CodeInfo.UnknownImageForeground,
            CodeInfo.UnknownImageText
        )

        /**
         * Creates wrapper of the code information from raw barcode.
         * @param barcode Raw barcode data.
         */
        public fun fromRawbarcode(barcode: RawBarcode): CodeInfo{
            var image: Bitmap = CodeInfo.UnknownImage
            if (barcode.image != null){
                val bcodeImg: Image = barcode.image
                val bcodeBmap: Bitmap? = ImageUtils.toBitmap(bcodeImg)
                if (bcodeBmap != null){
                    image = bcodeBmap
                }
            }
            return CodeInfo(
                LocalDateTime.now(),
                CodeType.fromBarcode(barcode.barcode),
                image,
                barcode.position.toRectangle().toRect(),
                DataType.fromBarcode(barcode.barcode),
                barcode.barcode.rawValue ?: "",
                barcode.barcode.rawBytes?.size?.toDouble() ?: 0.0,
                MapUtils.barcodeToMap(barcode.barcode)
            )
        }
    }


}

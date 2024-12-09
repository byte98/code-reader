package cz.skodaj.codereader.model

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Rect
import android.media.Image
import android.os.Parcel
import android.os.Parcelable
import android.util.Base64
import android.util.Log
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
    private val image: Bitmap?

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
     * Raw data in binary form.
     */
    private val rawBytes: ByteArray

    /**
     * Creates new holder of basic information about code.
     * @param creationDate Date and time of creation.
     * @param codeType Type of code.
     * @param image Image containing code.
     * @param position Position of the code in the image.
     * @param dataType Type of data stored in code.
     * @param data Data stored in code.
     * @param bytes: Raw data in binary form.
     * @param size Size of data stored in code.
     * @param dataFields Fields of data stored in code (if available).
     */
    public constructor(
        creationDate: LocalDateTime,
        codeType: CodeType,
        image: Bitmap?,
        position: Rect,
        dataType: DataType,
        data: String,
        bytes: ByteArray,
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
        this.rawBytes = bytes
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
     * @return Image containing code,
     *         or NULL if no image is defined.
     */
    public fun getImage(): Bitmap?{
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

    /**
     * Gets raw data in binary form.
     * @return Array of bytes with data in binary form.
     */
    public fun getRawBytes(): ByteArray{
        return this.rawBytes
    }

    /**
     * Gets raw data in form of base64 encoded string.
     * @return String representing raw binary data in form of base64 encoded string.
     */
    public fun getRawBase64(): String{
        return Base64.encodeToString(this.rawBytes, Base64.DEFAULT).trim()
    }
}

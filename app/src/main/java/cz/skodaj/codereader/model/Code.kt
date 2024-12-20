package cz.skodaj.codereader.model

import android.graphics.Bitmap
import android.graphics.Rect
import android.media.Image
import java.time.LocalDateTime

/**
 * Class which represents scanned code.
 */
class Code: CodeInfo{

    /**
     * Identifier of code in database.
     */
    private val id: Long

    /**
     * Folder to which code belongs to.
     */
    private var folder: Folder

    /**
     * Name of code.
     */
    private var name: String

    /**
     * Description of code.
     */
    private var description: String

    /**
     * Date and time of last modification of the code.
     */
    private var modified: LocalDateTime

    /**
     * Creates new code.
     * @param id Identifier of code in database.
     * @param folder Folder to which code belongs to.
     * @param name Name of code.
     * @param description Description of code.
     * @param creationDate Date and time of creation.
     * @param modificationDate Date and time of last modification.
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
        id: Long,
        folder: Folder,
        name: String,
        description: String,
        creationDate: LocalDateTime,
        modificationDate: LocalDateTime,
        codeType: CodeType,
        image: Bitmap?,
        position: Rect,
        dataType: DataType,
        data: String,
        bytes: ByteArray,
        size: Double,
        dataFields: Map<String, String> = emptyMap()
    ): super(creationDate, codeType, image, position, dataType, data, bytes, size, dataFields){
        this.id = id
        this.folder = folder
        this.name = name
        this.description = description
        this.modified = modificationDate
    }

    /**
     * Gets identifier of code in database.
     * @return Identifier of code in database.
     */
    public fun getId(): Long{
        return this.id
    }

    /**
     * Gets folder to which code belongs to.
     * @return Folder to which code belongs to.
     */
    public fun getFolder(): Folder{
        return this.folder
    }

    /**
     * Sets folder to which code belongs to.
     * @param folder New folder to which code belongs to.
     */
    public fun setFolder(folder: Folder){
        this.folder = folder
    }

    /**
     * Gets name of code.
     * @return String containing name of code.
     */
    public fun getName(): String{
        return this.name
    }

    /**
     * Sets description of code.
     * @param description New description of code.
     */
    public fun setDescription(description: String){
        this.description = description
    }

    /**
     * Gets description of code.
     * @return String containing description of code.
     */
    public fun getDescription(): String{
        return this.description
    }

    /**
     * Sets date and time of last modification.
     * @param date New date and time of last modification.
     */
    public fun setModificationDate(date: LocalDateTime){
        this.modified = date
    }

    /**
     * Gets date and time of last modification.
     * @return Date and time of last modification.
     */
    public fun getModificationDate(): LocalDateTime{
        return this.modified
    }
}
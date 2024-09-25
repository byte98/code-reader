package cz.skodaj.codereader.model.services

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.Rect
import android.media.Image
import cz.skodaj.codereader.model.*
import cz.skodaj.codereader.model.contract.CodeContract
import cz.skodaj.codereader.model.db.CodeDatabaseHelper
import cz.skodaj.codereader.utils.DateUtils
import cz.skodaj.codereader.utils.ImageUtils
import cz.skodaj.codereader.utils.MapUtils
import java.time.LocalDateTime

/**
 * Service which handles database operations over codes.
 */
class CodeService(

    /**
     * Provider of communication with the database.
     */
    private val db: CodeDatabaseHelper
) {

    private companion object{

        /**
         * Encodes position to the string.
         * @param position Position which will be encoded.
         * @return String containing encoded position.
         */
        private fun positionToString(position: Rect): String{
            val reti: MutableMap<String, String> = mutableMapOf()
            reti.put("top", position.top.toString())
            reti.put("left", position.left.toString())
            reti.put("bottom", position.bottom.toString())
            reti.put("right", position.right.toString())
            return MapUtils.toString(reti)
        }

        /**
         * Decodes position from the string.
         * @param string String containing encoded position.
         * @return Position (represented by rectangle) decoded from string.
         */
        private fun stringToPosision(string: String): Rect{
            val data: Map<String, String> = MapUtils.fromString(string)
            var top: Int = 0
            var left: Int = 0
            var bottom: Int = 0
            var right: Int = 0
            val topData: String? = data.get("top")
            val leftData: String? = data.get("left")
            val bottomData: String? = data.get("bottom")
            val rightData: String? = data.get("right")
            if (topData != null) top = topData.toIntOrNull() ?: 0
            if (leftData != null) left = leftData.toIntOrNull() ?: 0
            if (bottomData != null) bottom = bottomData.toIntOrNull() ?: 0
            if (rightData != null) right = rightData.toIntOrNull() ?: 0
            return Rect(left, top, right, bottom)
        }

    }

    /**
     * Creates new code in the database.
     * @param folder Folder to which code belongs to.
     * @param name Name of code.
     * @param description Description of code.
     * @param creationDate Date and time of creation.
     * @param codeType Type of code.
     * @param image Image containing code.
     * @param position Position of the code in the image.
     * @param dataType Type of data stored in code.
     * @param data Data stored in code.
     * @param dataFields Fields of data stored in code (if available).
     * @return Newly created code in the database,
     *         or NULL if code cannot be created.
     */
    public fun create(
        folder: Folder,
        name: String,
        description: String,
        creationDate: LocalDateTime,
        codeType: CodeType,
        image: Bitmap,
        position: Rect,
        dataType: DataType,
        data: String,
        dataFields: Map<String, String> = emptyMap()
    ): Code?{
        var reti: Code? = null
        val dbase: SQLiteDatabase? = this.db.writableDatabase
        if (dbase != null){
            val inData: ContentValues = ContentValues().apply {
                put(CodeContract.CodeEntry.COLUMN_FOLDER, folder.id)
                put(CodeContract.CodeEntry.COLUMN_NAME, name)
                put(CodeContract.CodeEntry.COLUMN_DESCRIPTION, description)
                put(CodeContract.CodeEntry.COLUMN_CREATED, DateUtils.datetimeToDouble(creationDate))
                put(CodeContract.CodeEntry.COLUMN_CODETYPE, codeType.toString())
                put(CodeContract.CodeEntry.COLUMN_IMAGE, ImageUtils.toBase64(image))
                put(CodeContract.CodeEntry.COLUMN_POSITION, CodeService.positionToString(position))
                put(CodeContract.CodeEntry.COLUMN_DATATYPE, dataType.toString())
                put(CodeContract.CodeEntry.COLUMN_DATA, data)
                put(CodeContract.CodeEntry.COLUMN_DATAFIELDS, MapUtils.toString(dataFields))
            }
            val newId: Long? = dbase.insert(CodeContract.CodeEntry.TABLE_NAME, null, inData)
            if (newId != null){
                reti = Code(
                    newId,
                    folder,
                    name,
                    description,
                    creationDate,
                    codeType,
                    image,
                    position,
                    dataType,
                    data,
                    dataFields
                )
            }
        }
        return reti
    }

    /**
     * Creates new code in the database.
     * @param folder Folder to which code belongs to.
     * @param name Name of code.
     * @param description Description of code.
     * @param info Additional information about code.
     * @return Newly created code in the database,
     *         or NULL if code cannot be created.
     */
    public fun create(
        folder: Folder,
        name: String,
        description: String,
        info: CodeInfo
    ): Code?{
        val fields: MutableMap<String, String> = mutableMapOf()
        for(key: String in info.getDataFields()){
            fields.put(key, info.getDataField(key) ?: "")
        }
        return this.create(
            folder,
            name,
            description,
            info.getCreationDate(),
            info.getCodeType(),
            info.getImage(),
            info.getPosition(),
            info.getDataType(),
            info.getData(),
            fields
        )
    }

    /**
     * Reads code from database by its identifier.
     * @param id Identifier of wanted code.
     * @return Code with searched identifier,
     *         or NULL if there is no such code.
     */
    public fun read(id: Long): Code?{
        var reti: Code? = null
        val dbase: SQLiteDatabase? = this.db.readableDatabase
        if (dbase != null){
            val projection: Array<String> = CodeContract.CodeEntry.ALL_COLUMNS
            val selection: String = "${CodeContract.CodeEntry.COLUMN_ID} = ?"
            val selectionArgs: Array<String> = arrayOf(id.toString())

        }
        return reti
    }
}
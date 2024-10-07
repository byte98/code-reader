package cz.skodaj.codereader.model.services

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.Rect
import android.media.Image
import androidx.core.database.getDoubleOrNull
import androidx.core.database.getIntOrNull
import androidx.core.database.getLongOrNull
import androidx.core.database.getStringOrNull
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
    private val db: CodeDatabaseHelper,

    /**
     * Service which handles operations over folders table.
     */
    private val folders: FolderService
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
     * @param size Size of data stored in code.
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
        size: Int,
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
                put(CodeContract.CodeEntry.COLUMN_SIZE, size)
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
                    size,
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
            info.getSize(),
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
            val cursor: Cursor = dbase.query(
                CodeContract.CodeEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
            )
            if (cursor != null){
                with(cursor){
                    while(moveToNext()){
                        reti = this@CodeService.readCursor(this)
                        break
                    }
                }

            }
        }
        return reti
    }

    /**
     * Gets all available codes in folder.
     * @param folder Folder which codes will be returned.
     * @return Array with codes which are located inside passed folder.
     */
    public fun read(folder: Folder): Array<Code>{
        var reti: MutableList<Code> = mutableListOf()
        val dbase: SQLiteDatabase = this.db.readableDatabase
        if (dbase != null){
            val projection: Array<String> = CodeContract.CodeEntry.ALL_COLUMNS
            val selection: String = "${CodeContract.CodeEntry.COLUMN_FOLDER} = ?"
            val selectionArgs: Array<String> = arrayOf(folder.id.toString())
            val cursor: Cursor = dbase.query(
                CodeContract.CodeEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
            )
            if (cursor != null){
                with(cursor){
                    while(moveToNext()){
                        val code: Code? = this@CodeService.readCursor(this)
                        if (code != null){
                            reti.add(code)
                        }
                    }
                }
            }
        }
        return reti.toTypedArray()
    }

    /**
     * Reads code data from cursor.
     * @param cursor Cursor which performs actual reading from the database.
     * @return Code with data from cursor,
     *         or NULL if code cannot be read.
     */
    private fun readCursor(cursor: Cursor): Code?{
        var reti: Code? = null

        // Declare variables for raw data from cursor
        var rawid: Long? = null
        var rawfolder: Long? = null
        var rawname: String? = null
        var rawdescription: String? = null
        var rawcreationDate: Double? = null
        var rawcodeType: String? = null
        var rawimage: String? = null
        var rawposition: String? = null
        var rawdataType: String? = null
        var rawdata: String? = null
        var rawdataFields: String? = null
        var rawSize: Int? = null

        // Declare variables which holds parsed values from raw data
        var id: Long? = null
        var folder: Folder? = null
        var name: String? = null
        var description: String? = null
        var creationDate: LocalDateTime? = null
        var codeType: CodeType? = null
        var image: Bitmap? = null
        var position: Rect? = null
        var dataType: DataType? = null
        var data: String? = null
        var dataFields: Map<String, String>? = null
        var size: Int? = null

        // Read data from cursor
        rawid = cursor.getLongOrNull(cursor.getColumnIndexOrThrow(CodeContract.CodeEntry.COLUMN_ID))
        rawfolder = cursor.getLongOrNull(cursor.getColumnIndexOrThrow(CodeContract.CodeEntry.COLUMN_FOLDER))
        rawname = cursor.getStringOrNull(cursor.getColumnIndexOrThrow(CodeContract.CodeEntry.COLUMN_NAME))
        rawdescription = cursor.getStringOrNull(cursor.getColumnIndexOrThrow(CodeContract.CodeEntry.COLUMN_DESCRIPTION))
        rawcreationDate = cursor.getDoubleOrNull(cursor.getColumnIndexOrThrow(CodeContract.CodeEntry.COLUMN_CREATED))
        rawcodeType = cursor.getStringOrNull(cursor.getColumnIndexOrThrow(CodeContract.CodeEntry.COLUMN_CODETYPE))
        rawimage = cursor.getStringOrNull(cursor.getColumnIndexOrThrow(CodeContract.CodeEntry.COLUMN_IMAGE))
        rawposition = cursor.getStringOrNull(cursor.getColumnIndexOrThrow(CodeContract.CodeEntry.COLUMN_POSITION))
        rawdataType = cursor.getStringOrNull(cursor.getColumnIndexOrThrow(CodeContract.CodeEntry.COLUMN_DATATYPE))
        rawdata = cursor.getStringOrNull(cursor.getColumnIndexOrThrow(CodeContract.CodeEntry.COLUMN_DATA))
        rawdataFields = cursor.getStringOrNull(cursor.getColumnIndexOrThrow(CodeContract.CodeEntry.COLUMN_DATAFIELDS))
        rawSize = cursor.getIntOrNull(cursor.getColumnIndexOrThrow(CodeContract.CodeEntry.COLUMN_SIZE))

        // Parse data read from cursor
        if (rawid != null) id = rawid
        if (rawfolder != null) folder = this.folders.read(rawfolder)
        if (rawname != null) name = rawname
        if (rawdescription != null) description = rawdescription
        if (rawcreationDate != null) creationDate = DateUtils.datetimeFromDouble(rawcreationDate)
        if (rawcodeType != null) codeType = CodeType.fromString(rawcodeType)
        if (rawimage != null) image = ImageUtils.toImage(rawimage)
        if (rawposition != null) position = CodeService.stringToPosision(rawposition)
        if (rawdataType != null) dataType = DataType.fromString(rawdataType)
        if (rawdata != null) data = rawdata
        if (rawdataFields != null) dataFields = MapUtils.fromString(rawdataFields) else dataFields = emptyMap()
        if (rawSize != null) size = rawSize

        // Perform data consistency check
        // If passed: create new code object
        if (
            id != null &&
            folder != null &&
            name != null &&
            description != null &&
            creationDate != null &&
            codeType != null &&
            image != null &&
            position != null &&
            dataType != null &&
            data != null &&
            dataFields != null &&
            size != null
        ){
            reti = Code(id, folder, name, description, creationDate, codeType, image, position, dataType, data, size, dataFields)
        }
        return reti
    }

    /**
     * Updates code in the database.
     * @param code Code which will be updated.
     */
    public fun update(code: Code){
        val dbase: SQLiteDatabase = this.db.writableDatabase
        if (dbase != null){
            val dataFields: MutableMap<String, String> = mutableMapOf()
            for(key: String in code.getDataFields()){
                dataFields.put(key, code.getDataField(key) ?: "")
            }
            val data: ContentValues = ContentValues().apply {
                put(CodeContract.CodeEntry.COLUMN_FOLDER, code.getFolder().id)
                put(CodeContract.CodeEntry.COLUMN_NAME, code.getName())
                put(CodeContract.CodeEntry.COLUMN_DESCRIPTION, code.getDescription())
                put(CodeContract.CodeEntry.COLUMN_CREATED, DateUtils.datetimeToDouble(code.getCreationDate()))
                put(CodeContract.CodeEntry.COLUMN_CODETYPE, code.getCodeType().toString())
                put(CodeContract.CodeEntry.COLUMN_IMAGE, ImageUtils.toBase64(code.getImage()))
                put(CodeContract.CodeEntry.COLUMN_POSITION, CodeService.positionToString(code.getPosition()))
                put(CodeContract.CodeEntry.COLUMN_DATATYPE, code.getDataType().toString())
                put(CodeContract.CodeEntry.COLUMN_DATA, code.getData())
                put(CodeContract.CodeEntry.COLUMN_DATAFIELDS, MapUtils.toString(dataFields))
                put(CodeContract.CodeEntry.COLUMN_SIZE, code.getSize())
            }
            val selection: String = "${CodeContract.CodeEntry.COLUMN_ID} = ?"
            val selectionArgs: Array<String> = arrayOf(code.getId().toString())
            dbase.update(
                CodeContract.CodeEntry.TABLE_NAME,
                data,
                selection,
                selectionArgs
            )
        }
    }

    /**
     * Deletes code from the database.
     * @param code Code which will be deleted.
     */
    public fun delete(code: Code){
        val dbase: SQLiteDatabase = this.db.writableDatabase
        if (dbase != null){
            val selection: String = "${CodeContract.CodeEntry.COLUMN_ID} = ?"
            val selectionArgs: Array<String> = arrayOf(code.getId().toString())
            dbase.delete(
                CodeContract.CodeEntry.TABLE_NAME,
                selection,
                selectionArgs
            )
        }
    }
}
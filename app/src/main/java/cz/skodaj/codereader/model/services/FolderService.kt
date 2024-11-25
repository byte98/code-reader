package cz.skodaj.codereader.model.services

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import cz.skodaj.codereader.model.Folder
import cz.skodaj.codereader.model.contract.FolderContract
import cz.skodaj.codereader.model.db.FolderDatabaseHelper

/**
 * Class which services folders in the database.
 */
class FolderService(

    /**
     * Helper of communication with database itself.
     */
    private val db: FolderDatabaseHelper
)
{
    /**
     * Creates new folder.
     * @param name Name of new folder.
     * @param description Description of new folder.
     * @param parent Parental folder of newly created folder.
     * @return Newly created folder, or NULL if new folder cannot be created.
     */
    public fun create(name: String, description: String, parent: Folder? = null): Folder?{
        var reti: Folder? = null
        val dbase: SQLiteDatabase? = this.db.writableDatabase
        if (dbase != null) {
            var parentId: Long? = null
            if (parent != null) {
                parentId = parent.getId()
            }
            val data: ContentValues = ContentValues().apply {
                put(FolderContract.FolderEntry.COLUMN_NAME, name)
                put(FolderContract.FolderEntry.COLUMN_DESCRIPTION, description)
                put(FolderContract.FolderEntry.COLUMN_PARENT, parentId)
            }
            val newId = dbase?.insert(FolderContract.FolderEntry.TABLE_NAME, null, data)
            if (newId != null && newId >= 0){
                reti = Folder(newId, name, description, parent)
            }
        }
        return reti
    }

    /**
     * Reads folder by its identifier.
     * @param id Identifier of folder.
     * @return Folder read by its identifier,
     *         or NULL if there is no such folder.
     */
    public fun read(id: Long): Folder?{
        var reti: Folder? = null
        val dbase: SQLiteDatabase? = this.db.readableDatabase
        if (dbase != null){
            val projection: Array<String> = FolderContract.FolderEntry.ALL_COLUMNS
            val selection: String = "${FolderContract.FolderEntry.COLUMN_ID} = ?"
            val selectionArgs: Array<String> = arrayOf(id.toString())
            val cursor: Cursor = dbase.query(
                FolderContract.FolderEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
            )
            var name: String? = null
            var desc: String? = null
            var prnt: Long? = null
            if (cursor != null){
                with(cursor){
                    while(moveToNext()){
                        name = getString(getColumnIndexOrThrow(FolderContract.FolderEntry.COLUMN_NAME))
                        desc = getString(getColumnIndexOrThrow(FolderContract.FolderEntry.COLUMN_DESCRIPTION))
                        prnt = getLong(getColumnIndexOrThrow(FolderContract.FolderEntry.COLUMN_PARENT))
                        break
                    }
                }
            }
            if (name != null && desc != null){
                var parent: Folder? = null
                if (prnt != null){
                    parent = this.read(prnt ?: -1 )
                }
                reti = Folder(id, name ?: "", desc ?: "", parent)
            }
        }
        return reti
    }

    /**
     * Reads all children folders.
     * @param parent Parental folder which children wil be read.
     * @return Array of all children folder for given parent.
     */
    public fun read(parent: Folder?): Array<Folder>{
        val reti: MutableList<Folder> = mutableListOf<Folder>()
        val dbase: SQLiteDatabase? = this.db.readableDatabase
        if (dbase != null){
            var parentId: Long? = null
            if (parent != null){
                parentId = parent.getId()
            }
            val projection: Array<String> = arrayOf(FolderContract.FolderEntry.COLUMN_ID)
            val ids: MutableList<Long> = mutableListOf<Long>()
            val selection: String = "${FolderContract.FolderEntry.COLUMN_PARENT} = ?"
            val selectionArgs: Array<String> = arrayOf(parentId.toString())
            val cursor: Cursor = dbase.query(
                FolderContract.FolderEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
            )
            with(cursor){
                while(moveToNext()){
                    ids.add(getLong(getColumnIndexOrThrow(FolderContract.FolderEntry.COLUMN_ID)))
                }
            }
            for (id: Long in ids){
                val f: Folder? = this.read(id)
                if (f != null){
                    reti.add(f)
                }
            }
        }
        return reti.toTypedArray()
    }

    /**
     * Updates folder in the database.
     * @param folder Folder which will be updated.
     * @return Number of affected rows.
     */
    public fun update(folder: Folder): Int{
        var reti = 0
        val dbase: SQLiteDatabase? = this.db.writableDatabase
        var parentId: Long? = null
        if (folder.getParent() != null){
            parentId = folder.getParent()?.getId()
        }
        if (dbase != null){
            val data: ContentValues = ContentValues().apply {
                put(FolderContract.FolderEntry.COLUMN_NAME, folder.getName())
                put(FolderContract.FolderEntry.COLUMN_DESCRIPTION, folder.getDescription())
                put(FolderContract.FolderEntry.COLUMN_PARENT, parentId)
            }
            reti = dbase.update(FolderContract.FolderEntry.TABLE_NAME, data, "${FolderContract.FolderEntry.COLUMN_ID} = ?", arrayOf(folder.getId().toString()))
        }
        return reti
    }
}
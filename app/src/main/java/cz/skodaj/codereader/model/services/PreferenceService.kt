package cz.skodaj.codereader.model.services

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.core.database.getLongOrNull
import androidx.core.database.getStringOrNull
import cz.skodaj.codereader.model.contract.PreferenceContract
import cz.skodaj.codereader.model.db.PreferenceDatabaseHelper
import cz.skodaj.codereader.model.preferences.Preference

/**
 * Class which serves communication with table of preferences.
 */
class PreferenceService {

    /**
     * Provider of communication to the database.
     */
    private val db: PreferenceDatabaseHelper

    /**
     * Creates new service used to communicate with table of preferences.
     * @param db Provider of communication to the database.
     */
    constructor(db: PreferenceDatabaseHelper){
        this.db  = db
    }

    /**
     * Creates new preference.
     * @param name Name of new preference.
     * @param value Value of new preference.
     * @return Newly created preference,
     *         or NULL if new preference cannot be created.
     */
    public fun create(name: String, value: String): Preference?{
        var reti: Preference? = null
        val dbase: SQLiteDatabase? = this.db.writableDatabase
        if (dbase != null){
            val data: ContentValues = ContentValues().apply{
                put(PreferenceContract.PreferenceEntry.COLUMN_NAME, name)
                put(PreferenceContract.PreferenceEntry.COLUMN_VALUE, value)
            }
            val newId: Long? = dbase?.insert(
                PreferenceContract.PreferenceEntry.TABLE_NAME,
                null,
                data
            )
            if (newId != null){
                reti = Preference(newId, name, value)
            }
        }
        return reti
    }

    /**
     * Reads preference from database by its identifier.
     * @param id Identifier of searched preference.
     * @return Preference with given identifier,
     *         or NULL if there is no such preference.
     */
    public fun read(id: Long): Preference?{
        var reti: Preference? = null
        val dbase: SQLiteDatabase? = this.db.readableDatabase
        if (dbase != null){
            val selection: String = "${PreferenceContract.PreferenceEntry.COLUMN_ID} = ?"
            val selectionArgs: Array<String> = arrayOf(id.toString())
            val projection: Array<String> = PreferenceContract.PreferenceEntry.ALL_COLUMNS
            val cursor: Cursor? = dbase?.query(
                PreferenceContract.PreferenceEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
            )
            if (cursor != null){
                var outId: Long? = null
                var outName: String? = null
                var outValue: String? = null
                with(cursor){
                    while(moveToNext()){
                        outId = getLongOrNull(getColumnIndexOrThrow(PreferenceContract.PreferenceEntry.COLUMN_ID))
                        outName = getStringOrNull(getColumnIndexOrThrow(PreferenceContract.PreferenceEntry.COLUMN_NAME))
                        outValue = getStringOrNull(getColumnIndexOrThrow(PreferenceContract.PreferenceEntry.COLUMN_VALUE))
                        break
                    }
                }
                if (outId != null && outName != null && outValue != null){
                    reti = Preference(outId ?: Long.MIN_VALUE, outName ?: "", outValue ?: "")
                }
            }
        }
        return reti
    }

    /**
     * Reads preference from database by its name.
     * @param name Name of searched preference.
     * @return Preference with given name,
     *         or NULL if there is no such preference.
     */
    public fun read(name: String): Preference?{
        var reti: Preference? = null
        val dbase: SQLiteDatabase? = this.db.readableDatabase
        if (dbase != null){
            val selection: String = "${PreferenceContract.PreferenceEntry.COLUMN_NAME} = ?"
            val selectionArgs: Array<String> = arrayOf(name)
            val projection: Array<String> = PreferenceContract.PreferenceEntry.ALL_COLUMNS
            val cursor: Cursor? = dbase?.query(
                PreferenceContract.PreferenceEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
            )
            if (cursor != null){
                var outId: Long? = null
                var outName: String? = null
                var outValue: String? = null
                with(cursor){
                    while(moveToNext()){
                        outId = getLongOrNull(getColumnIndexOrThrow(PreferenceContract.PreferenceEntry.COLUMN_ID))
                        outName = getStringOrNull(getColumnIndexOrThrow(PreferenceContract.PreferenceEntry.COLUMN_NAME))
                        outValue = getStringOrNull(getColumnIndexOrThrow(PreferenceContract.PreferenceEntry.COLUMN_VALUE))
                        break
                    }
                }
                if (outId != null && outName != null && outValue != null){
                    reti = Preference(outId ?: Long.MIN_VALUE, outName ?: "", outValue ?: "")
                }
            }
        }
        return reti
    }

    /**
     * Updates preference in the database.
     * @param pref Preference which will be updated.
     */
    public fun update(pref: Preference){
        val dbase: SQLiteDatabase? = this.db.writableDatabase
        if (dbase != null){
            val selection: String = "${PreferenceContract.PreferenceEntry.COLUMN_ID} = ?"
            val selectionArgs: Array<String> = arrayOf(pref.getId().toString())
            val data: ContentValues = ContentValues().apply{
                put(PreferenceContract.PreferenceEntry.COLUMN_NAME, pref.getName())
                put(PreferenceContract.PreferenceEntry.COLUMN_VALUE, pref.getValue())
            }
            dbase?.update(
                PreferenceContract.PreferenceEntry.TABLE_NAME,
                data,
                selection,
                selectionArgs
            )
        }
    }

    /**
     * Deletes preference from the database.
     * @param pref Preference which will be deleted.
     */
    public fun delete(pref: Preference){
        val dbase: SQLiteDatabase? = this.db.writableDatabase
        if (dbase != null){
            val selection: String = "${PreferenceContract.PreferenceEntry.COLUMN_ID} = ?"
            val selectionArgs: Array<String> = arrayOf(pref.getId().toString())
            dbase?.delete(
                PreferenceContract.PreferenceEntry.TABLE_NAME,
                selection,
                selectionArgs
            )
        }
    }
}
package cz.skodaj.codereader.model.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import cz.skodaj.codereader.configuration.Database
import cz.skodaj.codereader.model.contract.PreferenceContract

/**
 * Class which provides basic communication with table with preferences.
 */
class PreferenceDatabaseHelper(context: Context): SQLiteOpenHelper(context, Database.NAME, null, Database.VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(PreferenceContract.SQL_CREATE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(PreferenceContract.SQL_DELETE)
        this.onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        this.onUpgrade(db, oldVersion, newVersion)
    }
}
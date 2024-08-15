package cz.skodaj.codereader.model.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import cz.skodaj.codereader.model.contract.FolderContract

/**
 * Class which provides basic communication with table with folders.
 */
class FolderDatabaseHelper(context: Context): SQLiteOpenHelper(context, Database.NAME, null, Database.VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(FolderContract.SQL_CREATE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(FolderContract.SQL_DELETE)
        this.onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        this.onUpgrade(db, oldVersion, newVersion)
    }

}
package cz.skodaj.codereader.model.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import cz.skodaj.codereader.configuration.Database
import cz.skodaj.codereader.model.contract.CodeContract


/**
 * Class which provides basic communication with table with codes.
 */
class CodeDatabaseHelper(context: Context): SQLiteOpenHelper(context, Database.NAME, null, Database.VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CodeContract.SQL_CREATE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(CodeContract.SQL_DELETE)
        this.onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        this.onUpgrade(db, oldVersion, newVersion)
    }
}
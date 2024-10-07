package cz.skodaj.codereader.model.contract

import android.provider.BaseColumns

/**
 * Object which holds contract of the table which stores codes in the database.
 */
object CodeContract {

    /**
     * Object which defines one single row in codes table.
     */
    object CodeEntry: BaseColumns{

        /**
         * Name of table which stores codes in the database.
         */
        public final const val TABLE_NAME: String = "CODES"

        /**
         * Name of column with identifier of code in the database.
         */
        public final const val COLUMN_ID: String = "ID"

        /**
         * Name of column with identifier of folder, to which code belongs to.
         */
        public final const val COLUMN_FOLDER: String = "FOLDER"

        /**
         * Name of column with name of code.
         */
        public final const val COLUMN_NAME: String = "NAME"

        /**
         * Name of column with description of code.
         */
        public final const val COLUMN_DESCRIPTION: String = "DESCRIPTION"

        /**
         * Name of column with date and time of creation of code.
         */
        public final const val COLUMN_CREATED: String = "CREATED"

        /**
         * Name of column with type of code.
         */
        public final const val COLUMN_CODETYPE: String = "CTYPE"

        /**
         * Name of column with image containing code.
         */
        public final const val COLUMN_IMAGE: String = "IMG"

        /**
         * Name of column with position of code in the image.
         */
        public final const val COLUMN_POSITION: String = "POSITION"

        /**
         * Name of column with data type of data stored in the code.
         */
        public final const val COLUMN_DATATYPE: String = "DTYPE"

        /**
         * Name of column with data of code.
         */
        public final const val COLUMN_DATA: String = "DATA"

        /**
         * Name of column with data fields of code.
         */
        public final const val COLUMN_DATAFIELDS: String = "FIELDS"

        /**
         * Name of column with size of data stored in code.
         */
        public final const val COLUMN_SIZE: String = "SIZE"

        /**
         * Array with all columns of the table.
         */
        public final val ALL_COLUMNS: Array<String> = arrayOf(
            CodeContract.CodeEntry.COLUMN_ID,
            CodeContract.CodeEntry.COLUMN_FOLDER,
            CodeContract.CodeEntry.COLUMN_NAME,
            CodeContract.CodeEntry.COLUMN_DESCRIPTION,
            CodeContract.CodeEntry.COLUMN_CREATED,
            CodeContract.CodeEntry.COLUMN_CODETYPE,
            CodeContract.CodeEntry.COLUMN_IMAGE,
            CodeContract.CodeEntry.COLUMN_POSITION,
            CodeContract.CodeEntry.COLUMN_DATATYPE,
            CodeContract.CodeEntry.COLUMN_DATA,
            CodeContract.CodeEntry.COLUMN_DATAFIELDS,
            CodeContract.CodeEntry.COLUMN_SIZE
        )
    }

    /**
     * SQL query which creates table of codes.
     */
    public final const val SQL_CREATE: String =
        "CREATE TABLE ${CodeContract.CodeEntry.TABLE_NAME} ( " +
                "${CodeContract.CodeEntry.COLUMN_ID} INTEGER PRIMARY KEY NOT NULL, " +
                "${CodeContract.CodeEntry.COLUMN_FOLDER} INTEGER NOT NULL, " +
                "${CodeContract.CodeEntry.COLUMN_NAME} TEXT NOT NULL, " +
                "${CodeContract.CodeEntry.COLUMN_DESCRIPTION} TEXT NOT NULL, " +
                "${CodeContract.CodeEntry.COLUMN_CREATED} REAL NOT NULL, " +
                "${CodeContract.CodeEntry.COLUMN_CODETYPE} TEXT NOT NULL, " +
                "${CodeContract.CodeEntry.COLUMN_IMAGE} TEXT NOT NULL, " +
                "${CodeContract.CodeEntry.COLUMN_POSITION} TEXT NOT NULL, " +
                "${CodeContract.CodeEntry.COLUMN_DATATYPE} TEXT NOT NULL, " +
                "${CodeContract.CodeEntry.COLUMN_DATA} TEXT NOT NULL, " +
                "${CodeContract.CodeEntry.COLUMN_DATAFIELDS} TEXT NOT NULL ," +
                "${CodeContract.CodeEntry.COLUMN_SIZE} INTEGER NOT NULL" +
        ")"

    /**
     * SQL query which deletes table of codes.
     */
    public final const val SQL_DELETE: String = "DROP TABLE IF EXISTS ${CodeContract.CodeEntry.TABLE_NAME}"
}
package cz.skodaj.codereader.model.contract

import android.provider.BaseColumns

/**
 * Object which holds contract for table with preferences.
 */
object PreferenceContract {

    /**
     * Object which defines one single entry of the contract.
     */
    object PreferenceEntry: BaseColumns{

        /**
         * Name of table with preferences.
         */
        public final const val TABLE_NAME: String = "PREFERENCES"

        /**
         * Name of column with identifier of preference.
         */
        public final const val COLUMN_ID: String = "ID"

        /**
         * Name of column with name of preference.
         */
        public final const val COLUMN_NAME: String = "NAME"

        /**
         * Name of column with value of preference.
         */
        public final const val COLUMN_VALUE: String = "VALUE"

        /**
         * Array with all columns of the contract.
         */
        public final val ALL_COLUMNS: Array<String> = arrayOf(
            PreferenceContract.PreferenceEntry.COLUMN_ID,
            PreferenceContract.PreferenceEntry.COLUMN_NAME,
            PreferenceContract.PreferenceEntry.COLUMN_VALUE
        )
    }

    /**
     * String which represents SQL query which creates table with preferences.
     */
    public final const val SQL_CREATE: String =
        "CREATE TABLE ${PreferenceContract.PreferenceEntry.TABLE_NAME}(" +
                "${PreferenceContract.PreferenceEntry.COLUMN_ID} INTEGER PRIMARY KEY NOT NULL, " +
                "${PreferenceContract.PreferenceEntry.COLUMN_NAME} TEXT NOT NULL UNIQUE, " +
                "${PreferenceContract.PreferenceEntry.COLUMN_VALUE} TEXT NOT NULL" +
        ")"

    /**
     * String which represents SQL query which deletes table with preferences.
     */
    public final const val SQL_DELETE: String =
        "DROP TABLE IF EXISTS ${PreferenceContract.PreferenceEntry.TABLE_NAME}"
}
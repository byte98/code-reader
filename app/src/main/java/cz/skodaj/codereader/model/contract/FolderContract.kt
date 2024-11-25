package cz.skodaj.codereader.model.contract

import android.provider.BaseColumns
import cz.skodaj.codereader.model.Folder

/**
 * Object which defines contract for table with folders.
 */
object FolderContract {

    /**
     * Object which defines one single entry of contract.
     */
    object FolderEntry: BaseColumns{

        /**
         * Name of table with folders.
         */
        public final const val TABLE_NAME: String = "FOLDERS"

        /**
         * Name of column with identifiers of folders.
         */
        public final const val COLUMN_ID: String = "ID"

        /**
         * Name of column with names of folders.
         */
        public final const val COLUMN_NAME: String = "NAME"

        /**
         * Name of column with descriptions of folders.
         */
        public final const val COLUMN_DESCRIPTION: String = "DESCRIPTION"

        /**
         * Name of column with identifier of parental folder.
         */
        public final const val COLUMN_PARENT: String = "PARENT"

        /**
         * Array with all columns of the table.
         */
        public final val ALL_COLUMNS: Array<String> = arrayOf(
            FolderContract.FolderEntry.COLUMN_ID,
            FolderContract.FolderEntry.COLUMN_NAME,
            FolderContract.FolderEntry.COLUMN_DESCRIPTION,
            FolderContract.FolderEntry.COLUMN_PARENT
        )
    }

    /**
     * SQL query which creates table of folders.
     */
    public final const val SQL_CREATE: String =
            "CREATE TABLE ${FolderContract.FolderEntry.TABLE_NAME} (" +
                    "${FolderContract.FolderEntry.COLUMN_ID} INTEGER PRIMARY KEY NOT NULL," +
                    "${FolderContract.FolderEntry.COLUMN_NAME} TEXT NOT NULL," +
                    "${FolderContract.FolderEntry.COLUMN_DESCRIPTION} TEXT NOT NULL," +
                    "${FolderContract.FolderEntry.COLUMN_PARENT} INTEGER" +
            ")"

    /**
     * SQL query which creates root folder.
     */
    public final val SQL_CREATE_ROOT: String =
            "INSERT INTO ${FolderContract.FolderEntry.TABLE_NAME} (" +
                    "${FolderContract.FolderEntry.COLUMN_ID}, " +
                    "${FolderContract.FolderEntry.COLUMN_NAME}, " +
                    "${FolderContract.FolderEntry.COLUMN_DESCRIPTION})" +
            "VALUES (" +
                    "${Folder.Root.getId()}, " +
                    "'${Folder.Root.getName()}'," +
                    "'${Folder.Root.getDescription()}')"

    /**
     * SQL query which deletes table of folders.
     */
    public final const val SQL_DELETE: String = "DROP TABLE IF EXISTS ${FolderContract.FolderEntry.TABLE_NAME}"
}
package cz.skodaj.codereader.model.db

/**
 * Object which holds configuration of the database.
 */
object Database {

    /**
     * Name of the database.
     */
    const val NAME: String = "CODEREADER"

    /**
     * Name of file to which database will be saved.
     */
    const val FILE: String = "CODEREADER.DB"

    /**
     * Actual version of the database.
     */
    const val VERSION: Int = 1
}
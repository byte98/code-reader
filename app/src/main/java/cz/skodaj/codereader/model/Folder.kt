package cz.skodaj.codereader.model

/**
 * Class which represents folder.
 */
data class Folder(

    /**
     * Identifier of folder in the database
     */
    val id: Long,

    /**
     * Name of folder.
     */
    var name: String,

    /**
     * Description of folder.
     */
    var description: String,

    /**
     * Parental folder.
     */
    var parent: Folder? = Folder.Root

){
    companion object{

        /**
         * Root folder of the whole 'filesystem'.
         */
        val Root: Folder = Folder(
            0, "", "", null
        )
    }
}

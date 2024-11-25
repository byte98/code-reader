package cz.skodaj.codereader.model

/**
 * Class which represents folder.
 */
class Folder{

    /**
     * Identifier of folder in the database
     */
    private val id: Long

    /**
     * Name of folder.
     */
    private var name: String

    /**
     * Description of folder.
     */
    private var description: String

    /**
     * Parental folder.
     */
    private var parent: Folder? = Folder.Root

    /**
     * Creates new folder.
     * @param id Identifier of folder in the database.
     * @param name Name of folder.
     * @param description Description of folder.
     * @param parent Parental folder.
     */
    public constructor(id: Long, name: String, description: String, parent: Folder? = Folder.Root){
        this.id = id
        this.name = name
        this.description = description
        this.parent = parent
    }

    /**
     * Gets identifier of folder in database.
     * @return Identifier of folder in database.
     */
    public fun getId(): Long{
        return this.id
    }

    /**
     * Gets name of folder.
     * @return String representing name of folder.
     */
    public fun getName(): String{
        return this.name
    }

    /**
     * Sets name of folder.
     * @param name New name of folder.
     */
    public fun setName(name: String){
        this.name = name
    }

    /**
     * Gets description of folder.
     * @return String representing description of folder.
     */
    public fun getDescription(): String {
        return this.description
    }

    /**
     * Sets description of folder.
     * @param description New description of folder.
     */
    public fun setDescription(description: String){
        this.description = description
    }

    /**
     * Gets parental folder.
     * @return Folder which is parent to actual one,
     *         or NULL if folder has no parental folder.
     */
    public fun getParent(): Folder?{
        return this.parent
    }

    /**
     * Sets parental folder.
     * @param parent New folder which will became parent to actual one.
     */
    public fun setParent(parent: Folder){
        this.parent = parent
    }

    /**
     * Checks, whether folder is root folder (aka. there is no parental folder).
     * @return TRUE if folder is root folder,
     *         FALSE otherwise.
     */
    public fun isRoot(): Boolean{
        return this.id == Folder.Root.getId() || this.parent == null
    }

    /**
     * Gets full path of folder.
     * @param delimiter Delimiter of folders in the returned path.
     * @return String representing path to the folder.
     */
    public fun getPath(delimiter: String = "\\"): String{
        var reti: StringBuilder = StringBuilder()
        if (this.parent != null){
            reti.append(this.parent!!.getPath(delimiter))
            reti.append(this.getName())
            reti.append(delimiter)
        }
        else{
            reti.append(delimiter)
        }
        return reti.toString()
    }

    companion object{

        /**
         * Root folder of the whole 'filesystem'.
         */
        val Root: Folder = Folder(
            0, "", "", null
        )
    }
}

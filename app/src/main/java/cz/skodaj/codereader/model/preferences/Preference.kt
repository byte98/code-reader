package cz.skodaj.codereader.model.preferences

/**
 * Class representing one single preference of user.
 */
class Preference {

    /**
     * Identifier of preference in the database.
     */
    private val id: Long

    /**
     * Name of preference.
     */
    private val name: String

    /**
     * Value of preference.
     */
    private var value: String

    /**
     * Creates new preference.
     * @param id Identifier of preference in the database.
     * @param name Name of preference.
     * @param value Value of preference.
     */
    constructor(id: Long, name: String, value: String){
        this.id = id
        this.name = name
        this.value = value
    }

    /**
     * Gets identifier of preference.
     * @return Identifier of preference in the database.
     */
    public fun getId(): Long{
        return this.id
    }

    /**
     * Gets name of preference.
     * @return String representing name of preference.
     */
    public fun getName(): String{
        return this.name
    }

    /**
     * Gets value of preference.
     * @return String representing value of preference.
     */
    public fun getValue(): String{
        return this.value
    }

    /**
     * Sets value of preference.
     * @param value New value of preference.
     */
    public fun setPreference(value: String){
        this.value = value
    }
}
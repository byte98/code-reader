package cz.skodaj.codereader.model.preferences

import cz.skodaj.codereader.configuration.Preferences

/**
 * Enumeration of all available localizations of the application.
 */
enum class Localization {

    /**
     * English localization of the application.
     */
    ENGLISH("English", "en"),

    /**
     * Czech localization of the application.
     */
    CZECH("Čeština", "cs");

    /**
     * Name of localization which will be displayed to the end user.
     */
    private val displayName: String

    /**
     * Identifier of localization to be used as locale object.
     */
    private val locale: String

    /**
     * Creates new value of localization.
     * @param displayName Name of localization which will be displayed to the end user.
     * @param locale Identifier of localization used as locale object.
     */
    constructor(displayName: String, locale: String){
        this.displayName = displayName
        this.locale = locale
    }

    /**
     * Gets name of localization for the end user.
     * @return String representing name of localization for the end user.
     */
    public fun getDisplayName(): String{
        return this.displayName
    }

    /**
     * Gets identifier of locale.
     * @return String representing identifier which can be used to create locale object.
     */
    public fun getLocale(): String{
        return this.locale
    }

    public override fun toString(): String {
        var reti: String = ""
        when(this){
            ENGLISH -> reti = "ENGLISH"
            CZECH -> reti = "CZECH"
            else -> reti = Preferences.localization.toString()
        }
        return reti
    }

    companion object{

        /**
         * Creates localization from its string representation.
         * @param str String representation of the localization.
         * @return Localization parsed from its string representation.
         */
        public fun fromString(str: String): Localization{
            var reti: Localization = Preferences.localization
            val input: String = str.trim().uppercase()
            when(input){
                ENGLISH.toString() -> reti = ENGLISH
                CZECH.toString() -> reti = CZECH
                else -> reti = Preferences.localization
            }
            return reti
        }
    }
}
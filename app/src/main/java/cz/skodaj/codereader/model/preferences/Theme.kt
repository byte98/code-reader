package cz.skodaj.codereader.model.preferences

import cz.skodaj.codereader.configuration.Preferences

/**
 * Enumeration of all possible themes of the whole application.
 */
enum class Theme {

    /**
     * Light theme of the application.
     */
    LIGHT,

    /**
     * Dark theme of the application.
     */
    DARK,

    /**
     * Application theme should be same as the system one.
     */
    SYSTEM;

    public override fun toString(): String {
        var reti: String = ""
        when(this){
            LIGHT -> reti = "LIGHT"
            DARK -> reti = "DARK"
            SYSTEM -> reti = "SYSTEM"
            else -> reti = Preferences.theme.toString()
        }
        return reti
    }

    companion object{

        /**
         * Creates theme value from its string representation.
         * @param str String representation of theme.
         * @return Theme parsed from given string.
         */
        public fun fromString(str: String): Theme{
            val input: String = str.trim().uppercase()
            var reti: Theme = Preferences.theme
            when(input){
                "LIGHT" -> reti = LIGHT
                "DARK" -> reti = DARK
                "SYSTEM" -> reti = SYSTEM
                else -> reti = Preferences.theme
            }
            return reti
        }
    }
}
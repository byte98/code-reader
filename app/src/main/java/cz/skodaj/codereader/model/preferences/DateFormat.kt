package cz.skodaj.codereader.model.preferences

import cz.skodaj.codereader.configuration.Preferences
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * Enumeration of all available date formats known to application.
 */
enum class DateFormat {

    // TODO: There are more date formats needed.

    /**
     * Day, month, year short format separated by dot.
     */
    DMY_SHORT_DOT("d.M.yyyy"),

    /**
     * Day, month, year middle length format separated by dot.
     */
    DMY_MID_DOT("dd.MM.yyyy"),

    /**
     * Day, month, year long format separated by dot.
     */
    DMY_LONG_DOT("dd.MMMM.yyyy");

    /**
     * String which represents format usable by date formatter.
     */
    private val formatString: String

    /**
     * Creates new value which represents date format.
     * @param formatString String which represents format itself and is usable by date formatter later.
     */
    constructor(formatString: String){
        this.formatString = formatString
    }

    /**
     * Formats date according to format.
     * @param time Date data which will be formatted.
     * @return String representing date formatted according to format.
     */
    public fun format(date: LocalDate): String{
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern(this.formatString)
        return date.format(formatter)
    }

    /**
     * Gets string representation of format.
     * @return String representing format.
     */
    public override fun toString(): String{
        var reti: String
        when(this){
            DMY_SHORT_DOT -> reti = "DMY_SHORT_DOT"
            DMY_MID_DOT -> reti = "DMY_MID_DOT"
            DMY_LONG_DOT -> reti = "DMY_LONG_DOT"
            else -> reti = Preferences.dateFormat.toString()
        }
        return reti
    }

    companion object{

        /**
         * Creates date format from its string representation.
         * @param str String representation of date format.
         * @return Date format parsed from its string representation.
         */
        public fun fromString(str: String): DateFormat{
            var reti: DateFormat
            val input: String = str.trim().uppercase()
            when(input){
                DMY_SHORT_DOT.toString() -> reti = DMY_SHORT_DOT
                DMY_MID_DOT.toString() -> reti = DMY_MID_DOT
                DMY_LONG_DOT.toString() -> reti = DMY_LONG_DOT
                else -> reti = Preferences.dateFormat
            }
            return reti
        }

    }
}
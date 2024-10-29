package cz.skodaj.codereader.model.preferences

import cz.skodaj.codereader.configuration.Preferences
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * Enumeration of all time formats known to the application.
 */
enum class TimeFormat {

    /**
     * Short variant of 24 hour format.
     */
    SHORT_24("H:m:s"),

    /**
     * Middle length variant of 24 hour format.
     */
    MID_24("H:mm:ss"),

    /**
     * Long variant of 24 hour format.
     */
    LONG_24("HH:mm:ss"),

    /**
     * Short variant of 12 hour format.
     */
    SHORT_12("h:m:s a"),

    /**
     * Middle length variant of 12 hour format.
     */
    MID_12("h:mm:ss a"),

    /**
     * Long variant of 12 hour format.
     */
    LONG_12("hh:mm:ss a");

    /**
     * String representing format of time usable by time formatter.
     */
    private val formatString: String

    /**
     * Creates new value of time formats enumeration.
     * @param formatString String representing format usable by time formatter.
     */
    constructor(formatString: String){
        this.formatString = formatString
    }

    /**
     * Formats time according to format.
     * @param time Time data which will be formatted.
     * @return String representing time formatted according to format.
     */
    public fun format(time: LocalTime): String{
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern(this.formatString)
        return time.format(formatter)
    }

    public override fun toString(): String {
        var reti: String
        when(this){
            SHORT_24 -> reti = "SHORT_24"
            MID_24 -> reti = "MID_24"
            LONG_24 -> reti = "LONG_24"
            SHORT_12 -> reti = "SHORT_12"
            MID_12 -> reti = "MID_12"
            LONG_12 -> reti = "LONG_12"
            else -> reti = Preferences.timeFormat.toString()
        }
        return reti
    }

    companion object{

        /**
         * Parses time format from its string representation.
         * @param str String representing time format.
         * @return Time format parsed from its string representation.
         */
        public fun fromString(str: String): TimeFormat{
            var reti: TimeFormat
            val input: String = str.trim().uppercase()
            when(input){
                SHORT_24.toString() -> reti = SHORT_24
                MID_24.toString() -> reti = MID_24
                LONG_24.toString() -> reti = LONG_24
                SHORT_12.toString() -> reti = SHORT_12
                MID_12.toString() -> reti = MID_12
                LONG_12.toString() -> reti = LONG_12
                else -> reti = Preferences.timeFormat
            }
            return reti
        }
    }
}
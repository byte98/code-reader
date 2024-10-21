package cz.skodaj.codereader.model.preferences

/**
 * Enumeration of all available date time formats known to application.
 */
enum class DateTimeFormat {

    /**
     * Day, month, year format with long names separated by dot.
     */
    dmyLongDot,

    /**
     * Day, month, year format with short names separated by dot.
     */
    dmyShortDot,

    /**
     * Year, month, day format with long names separated by dot.
     */
    ymdLongDot,

    /**
     * Year, month, day format with short names separated by dot.
     */
    ymdShortDot,

    /**
     * Month, day, year format with long names separated by dot.
     */
    mdyLongDot,

    /**
     * Month, day, year format with short names separated by dot.
     */
    mdyShortDot,

    /**
     * Day, month, year format with long names separated by slash.
     */
    dmyLongSlash,

    /**
     * Day, month, year format with short names separated by slash.
     */
    dmyShortSlash,

    /**
     * Year, month, day format with long names separated by slash.
     */
    ymdLongSlash,

    /**
     * Year, month, day format with short names separated by slash.
     */
    ymdShortSlash,

    /**
     * Month, day, year format with long names separated by slash.
     */
    mdyLongSlash,

    /**
     * Month, day, year format with short names separated by slash.
     */
    mdyShortSlash,

    /**
     * Day, month, year format with long names separated by dash.
     */
    dmyLongDash,

    /**
     * Day, month, year format with short names separated by dash.
     */
    dmyShortDash,

    /**
     * Year, month, day format with long names separated by dash.
     */
    ymdLongDash,

    /**
     * Year, month, day format with short names separated by dash.
     */
    ymdShortDash,

    /**
     * Month, day, year format with long names separated by dash.
     */
    mdyLongDash,

    /**
     * Month, day, year format with short names separated by dash.
     */
    mdyShortDash;
}
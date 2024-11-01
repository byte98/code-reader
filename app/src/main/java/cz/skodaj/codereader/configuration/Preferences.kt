package cz.skodaj.codereader.configuration

import cz.skodaj.codereader.model.preferences.DateFormat
import cz.skodaj.codereader.model.preferences.Localization
import cz.skodaj.codereader.model.preferences.Theme
import cz.skodaj.codereader.model.preferences.TimeFormat

/**
 * Object which holds default values of preferences (ie. if it is not set by user).
 */
object Preferences {

    /**
     * Default format of dates.
     */
    val dateFormat: DateFormat = DateFormat.DMY_MID_DOT

    /**
     * Default format of times.
     */
    val timeFormat: TimeFormat = TimeFormat.LONG_24

    /**
     * Default theme of the application.
     */
    val theme: Theme = Theme.SYSTEM

    /**
     * Default localization of the application.
     */
    val localization: Localization = Localization.ENGLISH
}
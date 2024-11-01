package cz.skodaj.codereader.model.preferences

import android.content.Context
import android.content.res.Resources
import androidx.appcompat.app.AppCompatDelegate
import cz.skodaj.codereader.configuration.Preferences
import cz.skodaj.codereader.model.db.DatabaseFactory
import cz.skodaj.codereader.model.services.PreferenceService

/**
 * Class which groups all known preferences together.
 */
class PreferencesSet {

// <editor-fold desc="Preferences naming" defaultstate="collapsed">

    /**
     * Name of preference which holds actually used time format.
     */
    private val _TIMEFORMAT: String = "TIME_FORMAT"

    /**
     * Name of preference which holds actually used date format.
     */
    private val _DATEFORMAT: String = "DATE_FORMAT"

    /**
     * Name of preference which holds actual theme of the application.
     */
    private val _THEME: String = "THEME"

    /**
     * Name of preference which holds actual localization of the application.
     */
    private val _LOCALIZATION: String = "LOCALIZATION"

// </editor-fold>

    /**
     * Service which provides communication with the database.
     */
    private val db: PreferenceService

    /**
     * Handler which can perform theme switching.
     */
    private val themeSwitch: ThemeSwitch

    /**
     * Handler which can perform localization switching.
     */
    private val localizationSwitch: LocalizationSwitch

    /**
     * Creates new set which works with all known preferences of the application.
     * @param db Service which handles communication with the database.
     * @param themeSwitch Handler which can perform theme switching.
     * @param localizationSwitch Handler which can perform localization switching.
     */
    private constructor(db: PreferenceService, themeSwitch: ThemeSwitch, localizationSwitch: LocalizationSwitch){
        this.db = db
        this.themeSwitch = themeSwitch
        this.localizationSwitch = localizationSwitch
    }

    /**
     * Actual format used to display time data.
     */
    public var timeFormat: TimeFormat
    get() {
        return TimeFormat.fromString(this.getPreference(this._TIMEFORMAT, Preferences.timeFormat.toString()))
    }
    set(value) {
        this.setPreference(this._TIMEFORMAT, value.toString())
    }

    /**
     * Actual format used to display date data.
     */
    public var dateFormat: DateFormat
        get() {
            return DateFormat.fromString(this.getPreference(this._DATEFORMAT, Preferences.dateFormat.toString()))
        }
        set(value) {
            this.setPreference(this._DATEFORMAT, value.toString())
        }

    /**
     * Actual theme of the application.
     */
    public var theme: Theme
        get(){
            return Theme.fromString(this.getPreference(this._THEME, Preferences.theme.toString()))
        }
        set(value) {
            this.setPreference(this._THEME, value.toString())
            this.themeSwitch.switch(value)
        }

    /**
     * Actual localization of the application.
     */
    public var localization: Localization
        get() {
            return Localization.fromString(this.getPreference(this._LOCALIZATION, Preferences.localization.toString()))
        }
        set(value){
            this.setPreference(this._LOCALIZATION, value.toString())
            this.localizationSwitch.switch(value)
        }

    /**
     * Gets preference from the database.
     * @param name Name of the preference.
     * @param default Default value of the preference.
     * @return String representing raw value of the preference.
     */
    private fun getPreference(name: String, default: String): String{
        var reti: String = default
        val pref: Preference? = this.db.read(name)
        if (pref == null){
            this.db.create(name, reti)
        }
        else{
            reti = pref.getValue()
        }
        return reti
    }

    /**
     * Sets value of preference in the database.
     * @param name Name of preference.
     * @param value New value of preference.
     */
    private fun setPreference(name: String, value: String){
        val pref: Preference? = this.db.read(name)
        if (pref == null){
            this.db.create(name, value)
        }
        else{
            pref.setPreference(value)
            this.db.update(pref)
        }
    }

    companion object{

        /**
         * Creates new preferences set with values gathered from actual context.
         * @param context Actual context of the application.
         * @return Set of preferences with values from actual context.
         */
        public fun of(context: Context): PreferencesSet{
            val service: PreferenceService = DatabaseFactory(context, this::class).preferences()
            val themeSwitch: ThemeSwitch = ThemeSwitch(context)
            val localizationSwitch = LocalizationSwitch(context)
            return PreferencesSet(service, themeSwitch, localizationSwitch)
        }
    }
}
package cz.skodaj.codereader.model.preferences

import android.content.Context
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

// </editor-fold>

    /**
     * Service which provides communication with the database.
     */
    private val db: PreferenceService

    /**
     * Creates new set which works with all known preferences of the application.
     * @param context Actual context of the application.
     */
    constructor(context: Context){
        this.db = DatabaseFactory(context, this::class).preferences()
    }

    /**
     * Actual format used to display time data.
     */
    public var timeFormat: TimeFormat
    get() {
        var reti: TimeFormat = Preferences.timeFormat
        val pref: Preference? = this.db.read(this._TIMEFORMAT)
        if (pref == null){
            this.db.create(this._TIMEFORMAT, Preferences.timeFormat.toString())
        }
        else{
            reti = TimeFormat.fromString(pref.getValue())
        }
        return reti
    }
    set(value) {
        val pref: Preference? = this.db.read(this._TIMEFORMAT)
        if (pref == null){
            this.db.create(this._TIMEFORMAT, value.toString())
        }
        else{
            pref.setPreference(value.toString())
            this.db.update(pref)
        }
    }

    /**
     * Actual format used to display date data.
     */
    public var dateFormat: DateFormat
        get() {
            var reti: DateFormat = Preferences.dateFormat
            val pref: Preference? = this.db.read(this._DATEFORMAT)
            if (pref == null){
                this.db.create(this._DATEFORMAT, Preferences.dateFormat.toString())
            }
            else{
                reti = DateFormat.fromString(pref.getValue())
            }
            return reti
        }
        set(value) {
            val pref: Preference? = this.db.read(this._DATEFORMAT)
            if (pref == null){
                this.db.create(this._DATEFORMAT, value.toString())
            }
            else{
                pref.setPreference(value.toString())
                this.db.update(pref)
            }
        }
}
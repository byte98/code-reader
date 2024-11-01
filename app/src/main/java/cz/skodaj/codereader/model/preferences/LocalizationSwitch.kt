package cz.skodaj.codereader.model.preferences

import android.content.Context
import android.content.res.Configuration
import java.util.*

/**
 * Class which provides switching of actual localization of the application.
 */
class LocalizationSwitch {

    /**
     * Reference to the actual context of the application.
     */
    private val context: Context

    /**
     * Creates new switch of actual localization of the application.
     * @param context Actual context of the application.
     */
    constructor(context: Context){
        this.context = context
    }

    /**
     * Performs switch of actual localization of the application.
     * @param localization New localization of the application.
     */
    public fun switch(localization: Localization){
        val locale: Locale = Locale(localization.getLocale())
        Locale.setDefault(locale)

        val config = Configuration(this.context.resources.configuration)
        config.setLocale(locale)

        this.context.createConfigurationContext(config)

        if (this.context is android.app.Activity){
            this.context.recreate()
        }

    }

}
package cz.skodaj.codereader.model.preferences

import androidx.appcompat.app.AppCompatDelegate

/**
 * Class which holds theme switching of the application.
 */
class ThemeSwitch {

    /**
     * Switches theme of the application.
     * @param theme New theme of the application.
     */
    public fun switch(theme: Theme){
        var themeVal: Int = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        when (theme){
            Theme.LIGHT -> themeVal = AppCompatDelegate.MODE_NIGHT_NO
            Theme.DARK -> themeVal = AppCompatDelegate.MODE_NIGHT_YES
            Theme.SYSTEM -> themeVal = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
        this.switch(themeVal)
    }

    /**
     * Switches value of the theme of the application.
     * @param value New value of the theme of the application.
     */
    private fun switch(value: Int){
        AppCompatDelegate.setDefaultNightMode(value)
    }
}
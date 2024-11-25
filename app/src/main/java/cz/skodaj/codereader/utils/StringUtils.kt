package cz.skodaj.codereader.utils

import android.content.Context

/**
 * Object which holds some utility functions to work with string.
 */
object StringUtils {

    /**
     * Translates given string.
     * @param context Context which contains all available translations.
     * @param str String which will be translated.
     * @return Translated string,
     *         or <!UNKNOWN_STRING()> when no translation is available.
     */
    public fun translate(context: Context, str: String): String{
        var reti: String = "<!UNKNOWN_STRING(${str})>"
        val resourceId: Int = context.resources.getIdentifier(str, "string", context.packageName)
        if (resourceId != 0){
            reti = context.getString(resourceId)
        }
        return reti
    }
}
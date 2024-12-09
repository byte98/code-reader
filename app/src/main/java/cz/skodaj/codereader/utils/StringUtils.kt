package cz.skodaj.codereader.utils

import android.content.Context
import android.util.Log
import kotlin.random.Random

/**
 * Object which holds some utility functions to work with string.
 */
object StringUtils {

    /**
     * Translates given string.
     * @param context Context which contains all available translations.
     * @param str String which will be translated.
     * @return Translated string, or original string if translation cannot be found.
     */
    public fun translate(context: Context, str: String): String{
        var reti: String = str
        val resourceId: Int = context.resources.getIdentifier(str, "string", context.packageName)
        if (resourceId != 0){
            reti = context.getString(resourceId)
            Log.d(this::class.qualifiedName, "String '${str}' has been translated to '${reti}'")
        }
        else{
            Log.e(this::class.qualifiedName, "Translation of string '${str}' failed: translation not found!")
        }
        return reti
    }

    /**
     * Creates pseudo-random string.
     * @param length Length of string.
     * @param alphabet String which contains characters which are allowed to create string.
     * @return Pseudo-randomly generated string.
     */
    public fun random(length: Int, alphabet: String = "abcdefghijklmnoopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"): String{
        val reti: StringBuilder = StringBuilder()
        val random: Random = Random(System.currentTimeMillis())
        for(i: Int in 0 until length){
            reti.append(alphabet.get(random.nextInt(0, alphabet.length)))
        }
        return reti.toString()
    }
}
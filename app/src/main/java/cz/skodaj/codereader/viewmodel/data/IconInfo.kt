package cz.skodaj.codereader.viewmodel.data

import android.graphics.Typeface


/**
 * Class which holds some information about icon.
 * @param char Character representing icon itself.
 * @param font Font which should be used to display icon.
 */
data class IconInfo(
    val char: Char,
    val font: Typeface
)


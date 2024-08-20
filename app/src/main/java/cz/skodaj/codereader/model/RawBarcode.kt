package cz.skodaj.codereader.model

import com.google.mlkit.vision.barcode.common.Barcode

/**
 * Class which represents raw barcode data from ML Kit image analyzer.
 * @param barcode Data of barcode itself.
 * @param position Position of barcode.
 */
data class RawBarcode(val barcode: Barcode, val position: ScalableRectangle)

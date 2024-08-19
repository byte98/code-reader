package cz.skodaj.codereader.utils

import com.google.mlkit.vision.barcode.common.Barcode

/**
 * Interface abstracting all classes which should be able to work with barcodes.
 */
interface BarcodesCallback {

    /**
     * Function which will be called when barcode(s) has been scanned.
     * @param barcodes List with scanned barcodes, or empty list if no barcode has been scanned.
     */
    public fun barcodeScanned(barcodes: List<Barcode>): Unit
}
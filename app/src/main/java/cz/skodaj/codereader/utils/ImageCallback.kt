package cz.skodaj.codereader.utils

import com.google.mlkit.vision.common.InputImage

/**
 * Class which abstracts all objects which should have function to work with images.
 */
interface ImageCallback {

    /**
     * Function which will be called when image needs processing.
     * @param image Image which needs processing.
     */
    public fun processImage(image: InputImage): Unit
}
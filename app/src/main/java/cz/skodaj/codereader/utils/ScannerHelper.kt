package cz.skodaj.codereader.utils

import android.media.Image
import androidx.camera.core.Camera
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.ZoomSuggestionOptions
import com.google.mlkit.vision.common.InputImage

/**
 * Class which helps handling code scanner.
 */
@ExperimentalGetImage
class ScannerHelper: ImageCallback {

     /**
     * Class which prepares image from camera for analysis.
     */
    private class CameraAnalyzer: ImageAnalysis.Analyzer{

         /**
          * Function which will be called when image is prepared for analysis.
          */
         private val callback: ImageCallback

         /**
          * Creates new analyser of image from camera.
          * @param callback Function which will be called when image is prepared for analysis.
          */
         public constructor(callback: ImageCallback){
             this.callback = callback
         }

         override fun analyze(imageProxy: ImageProxy) {
             val mediaImage: Image? = imageProxy.image
             if (mediaImage != null) {
                 val image =
                     InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                 this.callback.processImage(image)
                 mediaImage.close()
             }
             imageProxy.close()
         }
    }

    /**
     * Options of scanner.
     */
    private val options: BarcodeScannerOptions

    /**
     * Handler of camera zoom.
     */
    private val zoom: ZoomHelper

    /**
     * Reference to the camera of the device.
     */
    private val camera: Camera

    /**
     * Analyzer of input from camera of the device.
     */
    private val cameraAnalyzer: ScannerHelper.CameraAnalyzer

    /**
     * ML Kit scanner which processes images.
     */
    private val scanner: BarcodeScanner

    /**
     * Creates new scanner of barcodes.
     * @param camera Reference to the camera of the device.
     * @param zoom Handler of camera zoom.
     */
    public constructor(camera: Camera, zoom: ZoomHelper){
        this.zoom = zoom
        this.camera = camera
        this.options = BarcodeScannerOptions.Builder()
            .setZoomSuggestionOptions(
                ZoomSuggestionOptions.Builder(object: ZoomSuggestionOptions.ZoomCallback{
                    override fun setZoom(ratio: Float): Boolean {
                        this@ScannerHelper.zoom.setLevel(ratio)
                        return true
                    }
                })
                    .setMaxSupportedZoomRatio(this.zoom.getMaxLevel())
                    .build()
            )
            .enableAllPotentialBarcodes()
            .build()
        this.cameraAnalyzer = ScannerHelper.CameraAnalyzer(this)
        this.scanner = BarcodeScanning.getClient(this.options)
    }

    /**
     * Gets analyzer of camera input.
     * @return Analyzer of input from camera.
     */
    public fun getCameraAnalyzer(): ImageAnalysis.Analyzer{
        return this.cameraAnalyzer
    }

    public override fun processImage(image: InputImage) {

    }
}
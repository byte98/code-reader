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
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.ExecutorService

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
             }
             imageProxy.close()
         }
    }

    /**
     * Options of scanner.
     */
    private lateinit var options: BarcodeScannerOptions

    /**
     * Handler of camera zoom.
     */
    private lateinit var zoom: ZoomHelper

    /**
     * Analyzer of input from camera of the device.
     */
    private val cameraAnalyzer: ImageAnalysis

    /**
     * ML Kit scanner which processes images.
     */
    private val scanner: BarcodeScanner

    /**
     * List of handlers of scanned barcodes.
     */
    private val handlers: MutableList<BarcodesCallback>

    /**
     * Creates new scanner of barcodes.
     */
    public constructor(){
        this.handlers = mutableListOf<BarcodesCallback>()
        this.cameraAnalyzer = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
        this.scanner = BarcodeScanning.getClient(this.options)
    }

    /**
     * Initializes scanner.
     * @param zoom Handler of camera zoom.
     * @param executor Executor of camera.
     */
    public fun init(zoom: ZoomHelper, executor: ExecutorService){
        this.zoom = zoom
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
        this.cameraAnalyzer.setAnalyzer(executor, ScannerHelper.CameraAnalyzer(this))
    }

    /**
     * Registers handler of scanned barcode.
     * @param handler Handler which will be registered.
     */
    public fun registerHandler(handler: BarcodesCallback){
        if (this.handlers.contains(handler) == false){
            this.handlers.add(handler)
        }
    }

    /**
     * Calls all registered handlers.
     * @param codes Codes which will be passed to the handlers.
     */
    private fun callHandlers(codes: List<Barcode>){
        for(handler: BarcodesCallback in this.handlers){
            handler.barcodeScanned(codes)
        }
    }

    /**
     * Gets analyzer of camera input.
     * @return Analyzer of input from camera.
     */
    public fun getCameraAnalyzer(): ImageAnalysis{
        return this.cameraAnalyzer
    }

    public override fun processImage(image: InputImage) {
        this.scanner.process(image)
            .addOnSuccessListener { barcodes->
                this.callHandlers(barcodes)
            }
            .addOnFailureListener{
                this.callHandlers(arrayListOf<Barcode>())
            }
    }
}
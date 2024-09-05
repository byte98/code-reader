package cz.skodaj.codereader.utils

import android.graphics.Rect
import android.media.Image
import android.util.Size
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.ZoomSuggestionOptions
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import cz.skodaj.codereader.model.RawBarcode
import cz.skodaj.codereader.model.ScalableRectangle
import cz.skodaj.codereader.model.messaging.Messenger
import cz.skodaj.codereader.model.messaging.Receiver
import cz.skodaj.codereader.model.messaging.messages.CodeScannedMessage

/**
 * Class which handles scanner of codes from images.
 */
@ExperimentalGetImage
class ScannerHelper: ImageAnalysis.Analyzer, Receiver{

    /**
     * Actually scanned code.
     */
    private val code: MutableLiveData<RawBarcode?>

    /**
     * Object which performs image scanning.
     */
    private lateinit var scanner: BarcodeScanner

    /**
     * Handler of camera zoom.
     */
    private lateinit var zoom: ZoomHelper

    /**
     * Flag, whether code has been scanned.
     */
    private var scanned: Boolean = false

    /**
     * Creates new handler of image scanning for codes.
     */
    public constructor(){
        this.code = MutableLiveData<RawBarcode?>();
        this.code.setValue(null)
    }

    /**
     * Initializes handler of image scanner.
     * @param zoom Handler of camera zoom.
     */
    public fun init(zoom: ZoomHelper){
        Messenger.default.register(CodeScannedMessage::class, this)
        this.zoom = zoom
        this.scanner = BarcodeScanning.getClient(BarcodeScannerOptions.Builder()
            .setZoomSuggestionOptions(ZoomSuggestionOptions.Builder(object: ZoomSuggestionOptions.ZoomCallback{
                public override fun setZoom(ratio: Float): Boolean {
                    this@ScannerHelper.zoom.setLevel(ratio)
                    return true
                }
            })
                .setMaxSupportedZoomRatio(zoom.getMaxLevel())
                .build())
            .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
            .enableAllPotentialBarcodes()
            .build()
        )

    }

    /**
     * Gets actually scanned code.
     * @return Live data with actually scanned code.
     */
    public fun getCode(): LiveData<RawBarcode?>{
        return this.code
    }

    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage: Image? = imageProxy.getImage()
        if (mediaImage != null) {
            val image: InputImage =
                InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            val task = this.scanner.process(image)
                .addOnSuccessListener {
                    if (this.scanned == false) {
                        if (it.isNotEmpty()) {
                            val barcode: Barcode = it.first()
                            val rect: Rect? = barcode.boundingBox
                            if (barcode != null && rect != null) {
                                this.code.setValue(
                                    RawBarcode(
                                        barcode,
                                        ScalableRectangle(
                                            rect,
                                            Size(
                                                image.width,
                                                image.height
                                            )
                                        ),
                                        image.mediaImage
                                    )
                                )
                                Messenger.default.send(CodeScannedMessage(true))
                            } else {
                                this.code.setValue(null)
                            }
                        } else {
                            this.code.setValue(null)
                        }
                    }
                    imageProxy.close()
                }
                .addOnFailureListener {
                    this.code.setValue(null)
                    imageProxy.close()
                }
        } else {
            this.code.setValue(null)
            imageProxy.close()
        }
    }

    override fun receive(message: Any) {
        if (message::class == CodeScannedMessage::class){
            this.scanned = (message as CodeScannedMessage).scanned
        }
    }
}
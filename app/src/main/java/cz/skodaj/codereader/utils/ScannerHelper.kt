package cz.skodaj.codereader.utils

import android.graphics.Rect
import android.media.Image
import android.util.Log
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
import cz.skodaj.codereader.model.*
import cz.skodaj.codereader.model.messaging.Messenger
import cz.skodaj.codereader.model.messaging.Receiver
import cz.skodaj.codereader.model.messaging.messages.CameraEnabledMessage
import cz.skodaj.codereader.model.messaging.messages.CodeScannedMessage
import java.time.LocalDateTime

/**
 * Class which handles scanner of codes from images.
 */
@ExperimentalGetImage
class ScannerHelper: CameraHelper, ImageAnalysis.Analyzer, Receiver{

    /**
     * Actually scanned code.
     */
    private val code: MutableLiveData<CodeInfo?>

    /**
     * Object which performs image scanning.
     */
    private lateinit var scanner: BarcodeScanner

    /**
     * Handler of camera zoom.
     */
    private lateinit var zoom: ZoomHelper

    /**
     * Creates new handler of image scanning for codes.
     */
    public constructor(): super(){
        Messenger.default.register(CameraEnabledMessage::class, this)
        this.code = MutableLiveData<CodeInfo?>();
        this.code.setValue(null)
    }

    /**
     * Initializes handler of image scanner.
     * @param zoom Handler of camera zoom.
     */
    public fun init(zoom: ZoomHelper){
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
    public fun getCode(): LiveData<CodeInfo?>{
        return this.code
    }

    override fun analyze(imageProxy: ImageProxy) {
        if (this.isActive()) {
            val mediaImage: Image? = imageProxy.getImage()
            if (mediaImage != null) {
                val image: InputImage =
                    InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                val task = this.scanner.process(image)
                    .addOnSuccessListener {
                        if (it.isNotEmpty()) {
                            var barcode: Barcode = it.first()
                            var found: Boolean = false
                            for(bcode: Barcode in it){
                                if (bcode.rawBytes != null && bcode.rawBytes?.size ?: 0 > 0){
                                    barcode = bcode
                                    found = true
                                    break
                                }
                            }
                            if (found) {
                                val imgH: Int = mediaImage.height
                                val imgW: Int = mediaImage.width
                                val rotationDegrees = imageProxy.imageInfo.rotationDegrees
                                val isLandscape = rotationDegrees == 90 || rotationDegrees == 270
                                val bitmap = ImageUtils.toBitmap(mediaImage)?.let {
                                    if (isLandscape) {
                                        ImageUtils.rotate(it, rotationDegrees.toFloat())
                                    } else it
                                }
                                val rect: Rect = barcode.boundingBox ?: Rect(0, 0, 0, 0)
                                if (barcode != null && rect != null) {
                                    this.code.setValue(
                                        CodeInfo(
                                            LocalDateTime.now(),
                                            CodeType.fromBarcode(barcode),
                                            bitmap,
                                            rect,
                                            DataType.fromBarcode(barcode),
                                            barcode.rawValue ?: "",
                                            barcode.rawBytes ?: ByteArray(0),
                                            barcode.rawBytes?.size?.toDouble() ?: 0.0,
                                            MapUtils.barcodeToMap(barcode)
                                        )
                                    )
                                    Log.d(
                                        this::class.qualifiedName,
                                        "Found code. Image size (h×w): ${imgH}×${imgW}. Code location: ${rect}"
                                    )
                                    Log.d(
                                        this::class.qualifiedName,
                                        "Code data (${barcode.rawValue?.length ?: 0}): ${barcode.rawValue}"
                                    )
                                    Messenger.default.send(CodeScannedMessage(true))
                                } else {
                                    this.code.setValue(null)
                                }
                            }
                        } else {
                            this.code.setValue(null)
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
        else{
            imageProxy.close()
        }
    }
}
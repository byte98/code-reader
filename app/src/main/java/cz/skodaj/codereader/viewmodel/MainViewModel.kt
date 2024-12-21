package cz.skodaj.codereader.viewmodel

import android.content.Context
import android.view.ScaleGestureDetector
import androidx.camera.core.Camera
import androidx.camera.core.ExperimentalGetImage
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import cz.skodaj.codereader.model.messaging.Messenger
import cz.skodaj.codereader.model.messaging.Receiver
import cz.skodaj.codereader.model.messaging.messages.CodeScannedMessage
import cz.skodaj.codereader.utils.FlashlightHelper
import cz.skodaj.codereader.utils.ScannerHelper
import cz.skodaj.codereader.utils.ZoomHelper
import cz.skodaj.codereader.viewmodel.connectors.ZoomConnector
import java.util.*

/**
 * View model of main activity.
 */
@ExperimentalGetImage
class MainViewModel: ViewModel() {

    /**
     * Handler of flash light.
     */
    private lateinit var flashlight: FlashlightHelper

    /**
     * Handler of zoom.
     */
    private lateinit var zoom: ZoomHelper

    /**
     * Connector of zooming to user interface.
     */
    private var zoomUI: ZoomConnector? = null

    /**
     * Handler of code scanning.
     */
    private val scanner: ScannerHelper = ScannerHelper()

    /**
     * Initializes all camera handlers.
     * @param camera Reference to the camera of the device.
     * @param context Context of the application.
     */
    public fun initCamera(camera: Camera, context: Context){
        this.flashlight = FlashlightHelper(camera, context)
        this.zoom = ZoomHelper(camera)
        this.scanner.init(this.zoom)
    }

    /**
     * Gets actual flashlight text.
     * @return Live data with actual text of state of flashlight.
     */
    public fun getFlashlightText(): LiveData<String>{
        return this.flashlight.getState()
    }

    /**
     * Gets actual flashlight icon.
     * @return Live data with actual icon of state of flashlight.
     */
    public fun getFlashlightIcon(): LiveData<String>{
        return this.flashlight.getIcon()
    }

    /**
     * Initializes interface of zooming.
     * @param context Actual context of the application.
     * @param ui Actual interface which will be controlled.
     * @return Detector of scale gesture.
     */
    public fun initZoomUI(context: Context, ui: ZoomConnector.ZoomUI): ScaleGestureDetector{
        this.zoomUI = ZoomConnector(ui, this.zoom)
        return this.zoomUI!!.getScaleListener(context)
    }

    /**
     * Shows user interface of zooming.
     */
    public fun showZoomUI(){
        this.zoomUI?.showUI()
    }

    /**
     * Toggles flashlight.
     */
    public fun toggleFlashlight(){
        if (this.flashlight.isOn()){
            this.flashlight.turnOff()
        }
        else{
            this.flashlight.turnOn()
        }
    }

    /**
     * Gets handler of image scanning.
     * @return Handler of image scanning.
     */
    public fun getScanner(): ScannerHelper{
        return this.scanner
    }


}
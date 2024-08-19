package cz.skodaj.codereader.viewmodel

import android.content.Context
import androidx.camera.core.Camera
<<<<<<< HEAD
import androidx.camera.core.ExperimentalGetImage
=======
>>>>>>> parent of bdf06a2 (Attempt to implement scanner removed)
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import cz.skodaj.codereader.utils.FlashlightHelper
import cz.skodaj.codereader.utils.ZoomHelper
<<<<<<< HEAD
import java.util.*
=======
>>>>>>> parent of bdf06a2 (Attempt to implement scanner removed)

/**
 * View model of main activity.
 */
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
     * Initializes all camera handlers.
     * @param camera Reference to the camera of the device.
     * @param context Context of the application.
     */
    public fun initCamera(camera: Camera, context: Context){
        this.flashlight = FlashlightHelper(camera, context)
        this.zoom = ZoomHelper(camera)
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
     * Gets actual zoom text.
     * @return Live data with actual zoom level as a text.
     */
    public fun getZoomText(): LiveData<String>{
        return Transformations.map(this.zoom.getLevelInt()) { level ->
            "$level %"
        }
    }

    /**
     * Gets minimal level of zoom.
     * @return Minimal zoom ratio supported by camera.
     */
    public fun getZoomMin(): Float{
        return this.zoom.getMinLevel()
    }

    /**
     * Gets maximal level of zoom.
     * @return Maximal zoom ratio supported by camera.
     */
    public fun getZoomMax(): Float{
        return this.zoom.getMaxLevel()
    }

    /**
     * Gets actual level of zoom.
     * @return Live data with actual zoom ratio.
     */
    public fun getZoomLevel(): LiveData<Float>{
        return this.zoom.getLevel()
    }

    /**
     * Gets actual static level of zoom.
     * @return Float with actual zoom ratio.
     */
    public fun getActualZoomLevel(): Float{
        return this.zoom.getActualLevel()
    }

    /**
     * Sets actual level of zoom.
     * @param level New level of zoom.
     */
    public fun setActualZoomLevel(level: Float){
        this.zoom.setLevel(level)
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

}
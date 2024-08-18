package cz.skodaj.codereader.viewmodel

import android.content.Context
import androidx.camera.core.Camera
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import cz.skodaj.codereader.view.FlashlightHelper

/**
 * View model of main activity.
 */
class MainViewModel: ViewModel() {

    /**
     * Handler of flash light.
     */
    private lateinit var flashlight: FlashlightHelper

    /**
     * Initializes flashlight.
     * @param camera Reference to the camera of the device.
     * @param context Context of the application.
     */
    public fun initFlashlight(camera: Camera, context: Context){
        this.flashlight = FlashlightHelper(camera, context)
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
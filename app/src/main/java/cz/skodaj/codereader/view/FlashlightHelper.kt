package cz.skodaj.codereader.view

import android.content.Context
import android.content.res.Resources
import android.hardware.camera2.CameraManager
import android.widget.TextView
import androidx.camera.core.Camera
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import cz.skodaj.codereader.R
import org.w3c.dom.Text

/**
 * Class which helps with handling flashlight.
 */
class FlashlightHelper {

    /**
     * Text which describes actual state of flashlight.
     */
    private val text: MutableLiveData<String>

    /**
     * String which contains actual icon of flashlight
     */
    private val icon: MutableLiveData<String>

    /**
     * Reference to device camera.
     */
    private val camera: Camera

    /**
     * Character representing icon of "flash light is on" state.
     */
    private final val ICON_ON: Char = Integer.decode("0xF00A").toChar()

    /**
     * Character representing icon of "flash light is off" state.
     */
    private final val ICON_OFF: Char = Integer.decode("0xF00B").toChar()

    /**
     * Flag, whether flash light is on.
     */
    private var isOn: Boolean = false

    /**
     * Context of the application.
     */
    private val context: Context

    /**
     * Creates new helper for flash light.
     * @param camera Reference to device camera.
     * @param context Actual context of the application.
     */
    constructor(camera: Camera, context: Context){
        this.camera = camera
        this.context = context
        this.text = MutableLiveData()
        this.icon = MutableLiveData()
    }

    /**
     * Sets state of actual flashlight hardware.
     * @param on When TRUE, flash light will be turned on,
     *           when FALSE, flash light will be turned off.
     */
    private fun setHardwareState(on: Boolean){
        this.isOn = on
        this.camera.cameraControl.enableTorch(on)
    }


    /**
     * Sets actual state to the view.
     * @param text Text describing actual state.
     * @param icon Icon of actual state.
     */
    private fun setView(text: String, icon: Char){
        this.text.setValue(text)
        this.icon.setValue(icon.toString())
    }

    /**
     * Turns on the flash light.
     */
    public fun turnOn(){
        val state: String = this.context.getString(R.string.flash_off)
        val icon: Char = this.ICON_ON
        this.setView(state, icon)
        this.setHardwareState(true)
    }

    /**
     * Turns off the flash light.
     */
    public fun turnOff(){
        val state: String = this.context.getString(R.string.flash_on)
        val icon: Char = this.ICON_OFF
        this.setView(state, icon)
        this.setHardwareState(false)
    }

    /**
     * Checks, whether flash light is on.
     * @return TRUE if flash light is on,
     *         FALSE otherwise.
     */
    public fun isOn(): Boolean{
        return this.isOn
    }

    /**
     * Gets actual icon of flashlight status.
     * @return Live data which contains actual icon of flashlight status.
     */
    public fun getIcon(): LiveData<String>{
        return this.icon
    }

    /**
     * Gets actual state of flashlight.
     * @return Live data which contains actual state of flashlight.
     */
    public fun getState(): LiveData<String>{
        return this.text
    }
}
package cz.skodaj.codereader.utils

import android.content.Context
import androidx.camera.core.Camera
import androidx.camera.core.ZoomState
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.Transformations
import kotlin.math.roundToInt


/**
 * Class which handles zoom of camera.
 */
class ZoomHelper {

    /**
     * Reference to device camera.
     */
    private val camera: Camera

    /**
     * Actual level of zoom.
     */
    private val zoom: MutableLiveData<Float>

    /**
     * Actual level of zoom as integer.
     */
    private val zoomInt: LiveData<Int>

    /**
     * Creates new handler of camera zoom.
     * @param camera Reference to the camera of the device.
     */
    constructor(camera: Camera){
        this.camera = camera
        this.zoom = MutableLiveData<Float>()
        this.zoom.setValue(this.camera.cameraInfo.zoomState?.value?.zoomRatio ?: 1f)
        this.zoomInt = Transformations.map(this.zoom){ floatVal ->
            (floatVal * 100).roundToInt()
        }
    }

    /**
     * Gets minimal level of zoom supported by camera.
     * @return Float representing minimal level of zoom supported by camera.
     */
    public fun getMinLevel(): Float{
        var reti: Float = 1.0f
        val zoomState: ZoomState? = camera.cameraInfo.zoomState.value
        if (zoomState != null){
            reti = zoomState?.minZoomRatio ?: 1.0f
        }
        return reti
    }

    /**
     * Gets maximal level of zoom supported by camera.
     * @return Float representing maximal level of zoom supported by camera.
     */
    public fun getMaxLevel(): Float{
        var reti: Float = 1.0f
        val zoomState: ZoomState? = camera.cameraInfo.zoomState.value
        if (zoomState != null){
            reti = zoomState?.maxZoomRatio ?: 1.0f
        }
        return reti
    }

    /**
     * Gets actual level of zoom.
     * @return Actual level of zoom.
     */
    public fun getLevel(): LiveData<Float>{
        return this.zoom
    }

    /**
     * Gets actual level of zoom as integer number (representing percents).
     * @return Actual level of zoom as integer.
     */
    public fun getLevelInt(): LiveData<Int>{
        return this.zoomInt
    }

    /**
     * Gets actual static level of zoom.
     * @return Actual static level of zoom.
     */
    public fun getActualLevel(): Float{
        return this.zoom.value?:1.0f
    }

    /**
     * Sets actual level of zoom.
     * @param level New level of zoom.
     */
    public fun setLevel(level: Float){
        this.zoom.setValue(level)
        this.setHardware(level)
    }

    /**
     * Sets zoom level to actual camera hardware.
     * @param level Level which will be set to the camera.
     */
    private fun setHardware(level: Float){
        this.camera.cameraControl.setZoomRatio(level)
    }
}
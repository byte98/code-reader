package cz.skodaj.codereader.viewmodel.connectors

import android.content.Context
import android.view.ScaleGestureDetector
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.widget.SeekBar
import android.widget.TextView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import cz.skodaj.codereader.utils.ZoomHelper
import kotlinx.coroutines.*
import kotlin.math.roundToInt

/**
 * Helper class which connects zoom helper to user interface.
 */
class ZoomConnector {

    /**
     * Class which holds all zooming user interface.
     * @property textValue Text view which displays actual value of zoom.
     * @property bar Bar which visually displays actual level of zoom.
     * @property menu Menu which displays all controls of zoom.
     * @param precision Precision of interface of zoom.
     * @param owner Owner of the interface.
     * @param timeout Timeout between UI is hidden (in milliseconds) (or 0 for not hiding at all).
     * @property show Animation used to show menu.
     * @property hide Animation used to hide menu.
     */
    data class ZoomUI(
        val textValue: TextView,
        val bar: SeekBar,
        val menu: ViewGroup,
        val precision: Int,
        val owner: LifecycleOwner,
        val timeout: Long = 0,
        val show: Animation? = null,
        val hide: Animation? = null
    )

    /**
     * Interface of zooming.
     */
    private val ui: ZoomConnector.ZoomUI

    /**
     * Provider of zooming functionality.
     */
    private val zoom: ZoomHelper

    /**
     * Job which hides zoom user interface.
     */
    private var hideJob: Job?

    /**
     * Creates new connector of zoom helper to the user interface.
     * @param ui Interface which displays actual state of zoom.
     * @param zoomHelper Provider of zooming functionality.
     */
    public constructor(ui: ZoomUI, zoomHelper: ZoomHelper){
        this.ui = ui
        this.zoom = zoomHelper
        this.hideJob = null
        this.init()
    }

    /**
     * Initializes user interface.
     */
    private fun init(){
        this.ui.bar.setMax((this.zoom.getMaxLevel() * this.ui.precision.toFloat()).roundToInt())
        this.ui.bar.setMin((this.zoom.getMinLevel() * this.ui.precision.toFloat()).roundToInt())
        this.ui.bar.setProgress((this.zoom.getActualLevel() * this.ui.precision.toFloat()).roundToInt())
        this.ui.bar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if ((this@ZoomConnector.zoom.getActualLevel() * this@ZoomConnector.ui.precision).roundToInt() != progress){
                    this@ZoomConnector.zoom.setLevel(progress.toFloat() / this@ZoomConnector.ui.precision.toFloat())
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        this.zoom.getLevel().observe(this.ui.owner) { level ->
            this.ui.bar.setProgress((level * this.ui.precision).roundToInt())    // SeekBar progress
            this.ui.textValue.setText("${this.zoom.getLevelInt().value ?: 0} %") // TextView text
            if (this.ui.menu.visibility != View.VISIBLE) {                        // Show & hide
                this.showUI()
            }
            this.restartHideUI()
        }
    }

    /**
     * Gets scale gesture detector.
     * @param context Actual context of the application.
     * @return Detector of scale gesture.
     */
    public fun getScaleListener(context: Context): ScaleGestureDetector{
        return ScaleGestureDetector(context, object: ScaleGestureDetector.SimpleOnScaleGestureListener(){
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                if (this@ZoomConnector.ui.menu.visibility != View.VISIBLE){
                    this@ZoomConnector.showUI()
                }
                var scale: Float = this@ZoomConnector.zoom.getActualLevel() * detector.scaleFactor
                if (scale < this@ZoomConnector.zoom.getMinLevel()) scale = this@ZoomConnector.zoom.getMinLevel()
                if (scale > this@ZoomConnector.zoom.getMaxLevel()) scale = this@ZoomConnector.zoom.getMaxLevel()
                this@ZoomConnector.zoom.setLevel(scale)
                return super.onScale(detector)
            }
        })
    }

    /**
     * Shows user interface controlling zoom.
     */
    public fun showUI(){
        this.ui.menu.apply {
            visibility = View.VISIBLE
            if (this@ZoomConnector.ui.show != null){
                this.startAnimation(this@ZoomConnector.ui.show)
            }
        }
    }

    /**
     * Restarts hiding of user interface controlling zoom.
     */
    private fun restartHideUI(){
        this.hideJob?.cancel()
        if (this.ui.timeout > 0){
            this.hideJob = CoroutineScope(Dispatchers.Main).launch {
                delay(this@ZoomConnector.ui.timeout)
                if (this@ZoomConnector.ui.hide != null){
                    this@ZoomConnector.ui.menu.startAnimation(this@ZoomConnector.ui.hide)
                }
                this@ZoomConnector.ui.menu.visibility = View.GONE
            }
        }
    }
}
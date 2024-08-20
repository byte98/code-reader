package cz.skodaj.codereader.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.Manifest
import android.animation.Animator
import android.animation.AnimatorInflater
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.RectF
import android.icu.number.Scale
import android.media.Image
import android.opengl.Visibility
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.view.ScaleGestureDetector
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.lifecycle.ProcessCameraProvider
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.SeekBar
import androidx.activity.result.ActivityResultLauncher
import androidx.camera.core.*
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.common.util.concurrent.ListenableFuture
import cz.skodaj.codereader.R

import cz.skodaj.codereader.databinding.ActivityMainBinding
import cz.skodaj.codereader.configuration.Android.PERMISSIONS
import cz.skodaj.codereader.viewmodel.MainViewModel
import cz.skodaj.codereader.viewmodel.ViewModelFactory
import kotlinx.coroutines.Job
import java.lang.Exception
import java.util.concurrent.Future
import kotlin.math.roundToInt
import kotlinx.coroutines.*
import kotlin.math.min

@ExperimentalGetImage
class MainActivity : AppCompatActivity() {

    /**
     * Binding to view.
     */
    private lateinit var viewBinding: ActivityMainBinding

    /**
     * View model of activity.
     */
    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this, ViewModelFactory()).get(MainViewModel::class.java)
    }

    /**
     * Executor of camera thread
     */
    private lateinit var cameraExecutor: ExecutorService


    /**
     * Flag, whether bottom menu is in detail mode or not.
     */
    private var bottomMenuDetail = false


    /**
     * Job which performs hiding of zoom layout.
     */
    private var hideZoomLayoutJob: Job? = null

    /**
     * Constant defining precision of zoom seek bar
     */
    private final val ZOOM_PRECISION = 10000

    /**
     * Delay between zoom bar show and hide (in milliseconds)
     */
    private final val ZOOM_DELAY: Long = 3000

    /**
     * Handler of whole application event loop.
     */
    private val handler: Handler = Handler(Looper.getMainLooper())

    /**
     * Checks, whether user allowed all required permissions.
     * @return TRUE if user allowed all required permissions,
     *         FALSE otherwise.
     */
    private fun checkPermissions(): Boolean{
        return PERMISSIONS.all{
            ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    /**
     * Launcher of activity which just asks for permissions.
     */
    private val activityResultLauncher: ActivityResultLauncher<Array<String>> = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    )
    {
        permissions ->
            var permissionGranted: Boolean = true
            permissions.entries.forEach{
                if (it.key in PERMISSIONS && it.value == false){
                    permissionGranted = false
                }
            }
            if (permissionGranted == false){
                this.displayPermissionErrorDialog()
            }
            else{
                this.startCamera()
            }
    }

    /**
     * Displays dialog informing about error during process
     * of acquiring permissions.
     */
    private fun displayPermissionErrorDialog(){
        Toast.makeText(baseContext,
            "Permission request denied",
            Toast.LENGTH_SHORT).show()
    }

    /**
     * Creates new request for permissions.
     */
    private fun requestPermissions(){
        this.activityResultLauncher.launch(PERMISSIONS)
    }

    /**
     * Starts camera and displays its preview.
     */
    private fun startCamera(){
        val cameraProviderFuture: ListenableFuture<ProcessCameraProvider> = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview: Preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(this.viewBinding.mainPreviewView.surfaceProvider)
                }
            val cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            try{
                cameraProvider.unbindAll()
                val analysis: ImageAnalysis = ImageAnalysis.Builder().setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build()
                analysis.setAnalyzer(ContextCompat.getMainExecutor(this), this.viewModel.getScanner())
                val camera: Camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, analysis)
                this.initCamera(camera)
            }
            catch (ex: Exception){}
        }, ContextCompat.getMainExecutor(this))
    }

    /**
     * Initializes camera.
     * @param camera Reference to the camera of the device.
     */
    private fun initCamera(camera: Camera){
        this.viewModel.initCamera(camera, this)

        // Flashlight
        this.viewModel.getFlashlightText().observe(this, Observer{ state ->
            this.viewBinding.mainTextViewFlashState.setText(state)
        })
        this.viewModel.getFlashlightIcon().observe(this, Observer{ icon ->
            this.viewBinding.mainTextViewFlashIcon.setText(icon)
        })

        // Zoom
        this.viewBinding.mainSeekbarZoom.setMax((this.viewModel.getZoomMax() * this.ZOOM_PRECISION).roundToInt())
        this.viewBinding.mainSeekbarZoom.setProgress((this.viewModel.getActualZoomLevel() * this.ZOOM_PRECISION).roundToInt())
        this.viewBinding.mainSeekbarZoom.setMin((this.viewModel.getZoomMin() * this.ZOOM_PRECISION).roundToInt())
        this.viewModel.getZoomLevel().observe(this, Observer{ level ->
            this.viewBinding.mainSeekbarZoom.setProgress((level * this.ZOOM_PRECISION).roundToInt())
        })
        this.viewBinding.mainSeekbarZoom.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if ((this@MainActivity.viewModel.getActualZoomLevel() * this@MainActivity.ZOOM_PRECISION).roundToInt() != progress){
                    this@MainActivity.viewModel.setActualZoomLevel(progress.toFloat() / this@MainActivity.ZOOM_PRECISION.toFloat())
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        this.viewModel.getZoomText().observe(this, Observer{text ->
            this.viewBinding.mainTextViewZoom.setText(text)
        })
        this.viewModel.getZoomLevel().observe(this, Observer{
            this.restartHideZoomTimer(
                AnimationUtils.loadAnimation(this, R.anim.fade_out)
            )
        })

    }

    /**
     * Shows layout with zoom controls.
     * @param show Animation used to show layout.
     * @param hide Animation used to hide layout.
     */
    private fun showZoomLayout(show: Animation, hide: Animation){
        this.viewBinding.mainLinearLayoutZoom.apply{
            visibility = View.VISIBLE
            startAnimation(show)
        }
        this.restartHideZoomTimer(hide)
    }

    /**
     * Restarts timer for hiding layout with zoom controls.
     * @param hide Animation used to hide layout.
     */
    private fun restartHideZoomTimer(hide: Animation){
        this.hideZoomLayoutJob?.cancel()
        this.hideZoomLayoutJob = CoroutineScope(Dispatchers.Main).launch{
            delay(this@MainActivity.ZOOM_DELAY)
            this@MainActivity.viewBinding.mainLinearLayoutZoom.startAnimation(hide)
            this@MainActivity.viewBinding.mainLinearLayoutZoom.visibility = View.GONE
        }
    }

    /**
     * Gets default rectangle for preview.
     * @return Default rectangle for preview.
     */
    private fun defaultRectangle(): RectF {
        val dimension: Float = min(this.viewBinding.mainPreviewView.width.toFloat(), this.viewBinding.mainPreviewView.height.toFloat()) * 0.7f
        return RectF(
            (this.viewBinding.mainPreviewView.width.toFloat() / 2f) - (dimension / 2f),
            (this.viewBinding.mainPreviewView.height.toFloat() / 2f) - (dimension / 2f),
            (this.viewBinding.mainPreviewView.width.toFloat() / 2f) + (dimension / 2f),
            (this.viewBinding.mainPreviewView.height.toFloat() / 2f) + (dimension / 2f)
        )
    }

    // <editor-fold defaultstate="collapsed" desc = "BUTTON HANDLERS">

    /**
     * Handles click on "flashlight" button in the bottom menu.
     * @param view View which has triggered the event.
     */
    public fun mainBottomMenuFlashClicked(view: View){
        this.viewModel.toggleFlashlight()
    }

    /**
     * Handles click on "toggle detail mode" button in the bottom menu.
     * @param view View which has triggered the event.
     */
    public fun mainBottomMenuToggleClicked(view: View){
        val menu: LinearLayout = this.viewBinding.mainBottomMenu
        if (this.bottomMenuDetail){
            this.bottomMenuDetail = false
            val animator: Animator = AnimatorInflater.loadAnimator(this, R.animator.slide_down)
            animator.setTarget(menu)
            animator.start()
        }
        else{
            this.bottomMenuDetail = true
            val animator: Animator = AnimatorInflater.loadAnimator(this, R.animator.slide_up)
            animator.setTarget(menu)
            animator.start()
        }
    }

    /**
     * Handles click on "zoom" button.
     * @param view View which has triggered the event.
     */
    public fun mainBottomMenuZoomClicked(view: View){
        this.showZoomLayout(
            AnimationUtils.loadAnimation(this, R.anim.fade_in),
            AnimationUtils.loadAnimation(this, R.anim.fade_out)
        )
    }

    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="DEFAULT ACTIVITY FUNCTIONS">

    override fun onCreate(savedInstanceState: Bundle?) {
        // Perform view binding
        super.onCreate(savedInstanceState)
        this.viewBinding = ActivityMainBinding.inflate(this.layoutInflater)
        this.setContentView(this.viewBinding.root)

        // Zoom
        this.viewBinding.mainLinearLayoutZoom.visibility = View.GONE
        val listener = object: ScaleGestureDetector.SimpleOnScaleGestureListener(){
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                if (this@MainActivity.viewBinding.mainLinearLayoutZoom.visibility != View.VISIBLE){
                    this@MainActivity.showZoomLayout(
                        AnimationUtils.loadAnimation(this@MainActivity, R.anim.fade_in),
                        AnimationUtils.loadAnimation(this@MainActivity, R.anim.fade_out)
                    )
                }
                var scale = this@MainActivity.viewModel.getActualZoomLevel() * detector.scaleFactor
                if (scale < this@MainActivity.viewModel.getZoomMin()) scale = this@MainActivity.viewModel.getZoomMin()
                if (scale > this@MainActivity.viewModel.getZoomMax()) scale = this@MainActivity.viewModel.getZoomMax()
                this@MainActivity.viewModel.setActualZoomLevel(scale)
                return true
            }
        }
        val scaleGestureDetector: ScaleGestureDetector = ScaleGestureDetector(this, listener)
        this.viewBinding.mainPreviewView.setOnTouchListener{_, event ->
            scaleGestureDetector.onTouchEvent(event)
            return@setOnTouchListener true
        }

        // Set up camera
        if (this.checkPermissions()){
            this.startCamera()
        }
        else{
            this.requestPermissions()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        this.cameraExecutor.shutdown()
    }

    //</editor-fold>


}
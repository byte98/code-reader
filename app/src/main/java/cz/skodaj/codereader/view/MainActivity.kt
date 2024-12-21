package cz.skodaj.codereader.view

import android.os.Bundle
import android.animation.Animator
import android.animation.AnimatorInflater
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.RectF
import android.util.Log
import android.util.Size
import android.view.ScaleGestureDetector
import androidx.core.content.ContextCompat
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.lifecycle.ProcessCameraProvider
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.SeekBar
import androidx.activity.result.ActivityResultLauncher
import androidx.camera.core.*
import androidx.camera.view.PreviewView
import androidx.core.graphics.toRectF
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.common.util.concurrent.ListenableFuture
import cz.skodaj.codereader.R

import cz.skodaj.codereader.databinding.ActivityMainBinding
import cz.skodaj.codereader.configuration.Android.PERMISSIONS
import cz.skodaj.codereader.model.messaging.Messenger
import cz.skodaj.codereader.model.messaging.Receiver
import cz.skodaj.codereader.model.messaging.messages.*
import cz.skodaj.codereader.model.preferences.PreferencesSet
import cz.skodaj.codereader.utils.Initializer
import cz.skodaj.codereader.viewmodel.MainViewModel
import cz.skodaj.codereader.viewmodel.ViewModelFactory
import cz.skodaj.codereader.viewmodel.connectors.ZoomConnector
import kotlinx.coroutines.Job
import kotlin.math.roundToInt
import kotlinx.coroutines.*
import kotlin.Exception
import kotlin.math.min

@ExperimentalGetImage
class MainActivity : MessagingActivity() {

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
     * Flag, whether bottom menu is in detail mode or not.
     */
    private var bottomMenuDetail = false


    /**
     * Constant defining precision of zoom seek bar
     */
    private final val ZOOM_PRECISION = 10000

    /**
     * Delay between zoom bar show and hide (in milliseconds)
     */
    private final val ZOOM_DELAY: Long = 3000

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
                Log.e(this::class.qualifiedName, "Permission request failed!")
            }
            else{
                this.startCamera()
            }
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
    private fun startCamera() {
        Log.d(this::class.qualifiedName, "Starting camera")

        val previewView: PreviewView = this.viewBinding.mainPreviewView
        val context: Context = this
        val owner: LifecycleOwner = this
        val cameraProviderFuture: ListenableFuture<ProcessCameraProvider> = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            try{
                cameraProvider.unbindAll()
                val preview: Preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }
                val camera = cameraProvider.bindToLifecycle(
                    owner,
                    cameraSelector,
                    preview

                )
                this.initCameraViewModel(camera)
                Log.d(this::class.qualifiedName, "Camera started")
            }
            catch (e: Exception){
                Log.e(this::class.qualifiedName, "Start of camera failed: ${e.message}")
                e.printStackTrace()
            }
        }, context.mainExecutor)
    }

    /**
     * Initializes camera for view model.
     * @param camera Reference to the camera of the device.
     */
    private fun initCameraViewModel(camera: Camera){
        Log.d(this::class.qualifiedName, "Initializing camera for view model...")
        this.viewModel.initCamera(camera, this)

        // Flashlight
        this.viewModel.getFlashlightText().observe(this, Observer{ state ->
            this.viewBinding.mainTextViewFlashState.setText(state)
        })
        this.viewModel.getFlashlightIcon().observe(this, Observer{ icon ->
            this.viewBinding.mainTextViewFlashIcon.setText(icon)
        })


        // Scanner
        this.viewModel.getScanner().getCode().observe(this, Observer{ code ->
            if (code == null) {
                this.viewBinding.mainRectangleView.setRectangle(this.defaultRectangle())
            }
            else {
                val previewSize = Size(
                    this.viewBinding.mainPreviewView.width,
                    this.viewBinding.mainPreviewView.height
                )
                val rectF: RectF = code.getPosition().toRectF()
                this.viewBinding.mainRectangleView.setRectangle(rectF)
                val intent: Intent = Intent(this, DetailActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                }
                Messenger.default.send(CameraEnabledMessage(enabled = false))
                Messenger.default.sendDelayed(DetailActivity::class, CodeInfoMessage(
                    code
                ))
                Messenger.default.registerOnce(DetailActivityFinishedMessage::class,
                    object : Receiver {
                        override fun receive(message: Any) {
                            Messenger.default.send(CameraEnabledMessage(enabled = true))
                        }
                    }
                )
                this.startActivity(intent)
            }
        })

        // Zoom
        this.viewBinding.mainLinearLayoutZoom.visibility = View.GONE
        val detector: ScaleGestureDetector = this.viewModel.initZoomUI(
            this,
            ZoomConnector.ZoomUI(
                this.viewBinding.mainTextViewZoom,
                this.viewBinding.mainSeekbarZoom,
                this.viewBinding.mainLinearLayoutZoom,
                this.ZOOM_PRECISION,
                this,
                this.ZOOM_DELAY,
                AnimationUtils.loadAnimation(this, R.anim.fade_in),
                AnimationUtils.loadAnimation(this, R.anim.fade_out)

            )
        )
        this.viewBinding.mainPreviewView.setOnTouchListener{_, event ->
            detector.onTouchEvent(event)
            return@setOnTouchListener true
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
        this.viewModel.showZoomUI()
    }

    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="DEFAULT ACTIVITY FUNCTIONS">

    override fun onCreate(savedInstanceState: Bundle?) {

        if (savedInstanceState == null) {
            // Initialize application
            // (there it is assumed, that here de facto starts program)
            Initializer.run()
            PreferencesSet.of(this).initApp()
        }

        // Perform view binding
        super.onCreate(savedInstanceState)
        this.viewBinding = ActivityMainBinding.inflate(this.layoutInflater)
        this.setContentView(this.viewBinding.root)



        // Draw rectangle
        this.viewBinding.mainRectangleView.setRectangle(this.defaultRectangle())

        // Init permissions
        if (this.checkPermissions()){
            this.startCamera()
        }
        else{
            this.requestPermissions()
        }
    }
    //</editor-fold>


}
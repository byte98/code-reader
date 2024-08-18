package cz.skodaj.codereader.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.Manifest
import android.animation.Animator
import android.animation.AnimatorInflater
import android.content.ContentValues
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
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
import java.lang.Exception
import java.util.concurrent.Future
import kotlin.math.roundToInt

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
     * Runnable which handles animations of zoom bar.
     */
    private var zoomRunnable: Runnable? = null

    /**
     * Constant defining precision of zoom seek bar
     */
    private final val ZOOM_PRECISION = 10000

    /**
     * Delay between zoom bar show and hide (in milliseconds)
     */
    private final val ZOOM_DELAY = 1000

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
                val camera: Camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview)
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
    }

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
        val zoomBar: LinearLayout = this.viewBinding.mainLinearLayoutZoom
        val fadeIn: Animation = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        val fadeOut: Animation = AnimationUtils.loadAnimation(this, R.anim.fade_out)
        val dataObserver: Observer<Float> = Observer<Float>{
            this.restartZoomAnimation(zoomBar, fadeIn, fadeOut)
        }
        this.viewModel.getZoomLevel().observe(this, dataObserver)
    }

    /**
     * Restarts and starts zoom animation.
     * @param zoomBar Zoom bar which animation will be restarted.
     * @param fadeIn
     */
    private fun restartZoomAnimation(zoomBar: LinearLayout, fadeIn: Animation, fadeOut: Animation){
        this.zoomRunnable?.let{
            this.handler.removeCallbacks(it)
        }
        zoomBar.startAnimation(fadeIn)
        fadeIn.setAnimationListener(object: Animation.AnimationListener{
            override fun onAnimationStart(animation: Animation) {
                // NOP
            }

            override fun onAnimationEnd(animation: Animation) {
                this@MainActivity.zoomRunnable = Runnable{
                    zoomBar.startAnimation(fadeOut)
                }
                handler.postDelayed(this@MainActivity.zoomRunnable!!, 1000)
            }

            override fun onAnimationRepeat(animation: Animation) {
                // NOP
            }
        })
    }

    //<editor-fold defaultstate="collapsed" desc="DEFAULT ACTIVITY FUNCTIONS">

    override fun onCreate(savedInstanceState: Bundle?) {
        // Perform view binding
        super.onCreate(savedInstanceState)
        this.viewBinding = ActivityMainBinding.inflate(this.layoutInflater)
        this.setContentView(this.viewBinding.root)

        // Zoom bar
        this.viewBinding.mainLinearLayoutZoom.alpha = 0.0f

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
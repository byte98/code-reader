package cz.skodaj.codereader.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.os.Build
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
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.camera.core.*
import androidx.camera.video.FallbackStrategy
import androidx.camera.video.MediaStoreOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.VideoRecordEvent
import androidx.core.content.PermissionChecker
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import com.google.common.util.concurrent.ListenableFuture
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.Locale

import cz.skodaj.codereader.databinding.ActivityMainBinding
import cz.skodaj.codereader.configuration.Android.PERMISSIONS
import cz.skodaj.codereader.viewmodel.MainViewModel
import cz.skodaj.codereader.viewmodel.ViewModelFactory
import java.lang.Exception
import java.util.concurrent.Future

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
     * Handler of flash light.
     */
    private lateinit var flashlight: FlashlightHelper

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
                this.initFlashlight(camera)
            }
            catch (ex: Exception){}
        }, ContextCompat.getMainExecutor(this))
    }

    /**
     * Initializes flashlight.
     * @param camera Reference to the camera of the device.
     */
    private fun initFlashlight(camera: Camera){
        this.viewModel.initFlashlight(camera, this)
        this.viewModel.getFlashlightText().observe(this, Observer{ state ->
            this.viewBinding.mainTextViewFlashState.setText(state)
        })
        this.viewModel.getFlashlightIcon().observe(this, Observer{ icon ->
            this.viewBinding.mainTextViewFlashIcon.setText(icon)
        })
    }

    /**
     * Handles click on "flashlight" button in the bottom menu.
     * @param view View which has triggered the event.
     */
    public fun mainBottomMenuFlashClicked(view: View){
        this.viewModel.toggleFlashlight()
    }

    //<editor-fold defaultstate="collapsed" desc="DEFAULT ACTIVITY FUNCTIONS">

    override fun onCreate(savedInstanceState: Bundle?) {
        // Perform view binding
        super.onCreate(savedInstanceState)
        this.viewBinding = ActivityMainBinding.inflate(this.layoutInflater)
        this.setContentView(this.viewBinding.root)

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
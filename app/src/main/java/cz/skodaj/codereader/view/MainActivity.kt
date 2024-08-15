package cz.skodaj.codereader.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.os.Build
import android.provider.MediaStore
import androidx.camera.core.ImageCapture
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
import androidx.camera.core.Preview
import androidx.camera.core.CameraSelector
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.video.FallbackStrategy
import androidx.camera.video.MediaStoreOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.VideoRecordEvent
import androidx.core.content.PermissionChecker
import com.google.common.util.concurrent.ListenableFuture
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.Locale

import cz.skodaj.codereader.databinding.ActivityMainBinding
import cz.skodaj.codereader.configuration.Android.PERMISSIONS
import java.lang.Exception
import java.util.concurrent.Future

class MainActivity : AppCompatActivity() {

    /**
     * Binding to view.
     */
    private lateinit var viewBinding: ActivityMainBinding

    /**
     * Executor of camera thread
     */
    private lateinit var cameraExecutor: ExecutorService

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
                cameraProvider.bindToLifecycle(this, cameraSelector, preview)
            }
            catch (ex: Exception){}
        }, ContextCompat.getMainExecutor(this))
    }


    //<editor-fold defaultstate="collapsed" desc="DEFAULT ACTIVITY FUNCTIONS">

    override fun onCreate(savedInstanceState: Bundle?) {
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
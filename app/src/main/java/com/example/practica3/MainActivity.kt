package com.example.practica3

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.media.MediaActionSound
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.text.InputType
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.practica3.databinding.ActivityMainBinding
import com.example.practica3.db.AppDatabase
import com.example.practica3.db.MediaItem
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityMainBinding
    private var imageCapture: ImageCapture? = null
    private var cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var mediaActionSound: MediaActionSound

    private var mediaRecorder: MediaRecorder? = null
    private var audioFilePath: String? = null
    private var isRecording = false
    private var isPaused = false

    private var audioTimerSeconds: Int? = null
    private val audioTimerHandler = Handler(Looper.getMainLooper())
    private lateinit var audioTimerRunnable: Runnable

    private val fusedLocationClient by lazy {
        LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }

        viewBinding.imageCaptureButton.setOnClickListener { takePhoto() }
        viewBinding.switchCameraButton.setOnClickListener { switchCamera() }
        viewBinding.startRecordButton.setOnClickListener { startRecording() }
        viewBinding.pauseRecordButton.setOnClickListener { pauseRecording() }
        viewBinding.stopRecordButton.setOnClickListener { stopRecording() }
        viewBinding.galleryButton.setOnClickListener { 
            val intent = Intent(this, GalleryActivity::class.java)
            startActivity(intent)
        }
        viewBinding.audioTimerButton.setOnClickListener { setAudioTimer() }

        cameraExecutor = Executors.newSingleThreadExecutor()
        mediaActionSound = MediaActionSound()
        mediaActionSound.load(MediaActionSound.SHUTTER_CLICK)
    }

    private fun setAudioTimer() {
        // ... (código existente)
    }

    private fun startRecording() {
        // ... (código existente)
    }

    private fun stopRecording() {
        // ... (código existente)
    }

    private fun captureAndSaveImage(outputOptions: ImageCapture.OutputFileOptions, location: Location?) {
        imageCapture?.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    mediaActionSound.play(MediaActionSound.SHUTTER_CLICK)
                    val msg = "Photo capture succeeded: ${output.savedUri}"
                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, msg)

                    lifecycleScope.launch {
                        val db = AppDatabase.getDatabase(applicationContext)
                        db.mediaItemDao().insert(MediaItem(
                            uri = output.savedUri.toString(),
                            isVideo = false,
                            date = System.currentTimeMillis(),
                            latitude = location?.latitude,
                            longitude = location?.longitude
                        ))
                    }
                }
            }
        )
    }

    // ... (El resto de los métodos permanecen igual)

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
        mediaRecorder?.release()
        mediaRecorder = null
        mediaActionSound.release()
    }

    // ... (El resto de los métodos y el companion object permanecen igual)
    // Añade aquí el resto de los métodos que ya tenías en tu MainActivity


    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewBinding.viewFinder.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder().build()

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )
            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Images")
            }
        }

        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(
                contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )
            .build()
        
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                captureAndSaveImage(outputOptions, location)
            }
        } else {
            captureAndSaveImage(outputOptions, null)
        }
    }

    private fun switchCamera() {
        cameraSelector = if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
            CameraSelector.DEFAULT_FRONT_CAMERA
        } else {
            CameraSelector.DEFAULT_BACK_CAMERA
        }
        startCamera()
    }

    private fun pauseRecording() {
        if (isRecording && !isPaused) {
            mediaRecorder?.pause()
            isPaused = true
            Toast.makeText(this, "Recording paused", Toast.LENGTH_SHORT).show()
        }
    }

    private fun resumeRecording() {
        mediaRecorder?.resume()
        isPaused = false
        Toast.makeText(this, "Recording resumed", Toast.LENGTH_SHORT).show()
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(
                    this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    companion object {
        private const val TAG = "CameraXApp"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS =
            mutableListOf(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }
}


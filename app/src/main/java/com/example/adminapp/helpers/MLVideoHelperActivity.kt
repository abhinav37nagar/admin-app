package com.example.adminapp.helpers

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.*
import android.media.Image
import android.os.Bundle
import android.view.Surface
import android.view.View
import android.widget.TextView
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.example.adminapp.R
import com.example.adminapp.helpers.vision.GraphicOverlay
import com.example.adminapp.helpers.vision.VisionBaseProcessor
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.common.util.concurrent.ListenableFuture
import java.io.ByteArrayOutputStream
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executor
import java.util.concurrent.Executors

abstract class MLVideoHelperActivity : AppCompatActivity() {
    private val executor: Executor = Executors.newSingleThreadExecutor()
    protected var previewView: PreviewView? = null

    @JvmField
    protected var graphicOverlay: GraphicOverlay? = null
    private var outputTextView: TextView? = null
    private var addFaceButton: ExtendedFloatingActionButton? = null
    private var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>? = null
    private var processor: VisionBaseProcessor<*>? = null
    private var imageAnalysis: ImageAnalysis? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_helper_new)
        previewView = findViewById(R.id.camera_source_preview)
        graphicOverlay = findViewById(R.id.graphic_overlay)
        outputTextView = findViewById(R.id.output_text_view)
        addFaceButton = findViewById(R.id.button_add_face)
        cameraProviderFuture = ProcessCameraProvider.getInstance(applicationContext)
        processor = setProcessor()
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA)
        } else {
            initSource()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (processor != null) {
            processor!!.stop()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CAMERA && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            initSource()
        }
    }

    protected fun setOutputText(text: String?) {
        outputTextView!!.text = text
    }

    private fun initSource() {
        cameraProviderFuture!!.addListener(
            {
                try {
                    val cameraProvider = cameraProviderFuture!!.get()
                    bindPreview(cameraProvider)
                } catch (e: ExecutionException) {
                    // No errors need to be handled for this Future.
                    // This should never be reached.
                } catch (e: InterruptedException) {
                }
            }, ContextCompat.getMainExecutor(
                applicationContext
            )
        )
    }

    private fun bindPreview(cameraProvider: ProcessCameraProvider) {
        val lensFacing = lensFacing
        val preview = Preview.Builder()
            .setTargetAspectRatio(AspectRatio.RATIO_4_3)
            .build()
        preview.setSurfaceProvider(previewView!!.surfaceProvider)
        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(lensFacing)
            .build()
        imageAnalysis = ImageAnalysis.Builder()
            .setTargetAspectRatio(AspectRatio.RATIO_4_3)
            .build()
        setFaceDetector(lensFacing)
        cameraProvider.bindToLifecycle(this, cameraSelector, imageAnalysis, preview)
    }

    /**
     * The face detector provides face bounds whose coordinates, width and height depend on the
     * preview's width and height, which is guaranteed to be available after the preview starts
     * streaming.
     */
    private fun setFaceDetector(lensFacing: Int) {
        previewView!!.previewStreamState.observe(this, object : Observer<PreviewView.StreamState?> {
            override fun onChanged(value: PreviewView.StreamState?) {
                if (value != PreviewView.StreamState.STREAMING) {
                    return
                }
                val preview = previewView!!.getChildAt(0)
                var width = preview.width * preview.scaleX
                var height = preview.height * preview.scaleY
                val rotation = preview.display.rotation.toFloat()
                if (rotation == Surface.ROTATION_90.toFloat() || rotation == Surface.ROTATION_270.toFloat()) {
                    val temp = width
                    width = height
                    height = temp
                }
                imageAnalysis!!.setAnalyzer(
                    executor,
                    createFaceDetector(width.toInt(), height.toInt(), lensFacing)
                )
                previewView!!.previewStreamState.removeObserver(this)
            }
        })
    }

    @OptIn(ExperimentalGetImage::class)
    private fun createFaceDetector(
        width: Int,
        height: Int,
        lensFacing: Int
    ): ImageAnalysis.Analyzer {
        graphicOverlay!!.setPreviewProperties(width, height, lensFacing)
        return ImageAnalysis.Analyzer label@{ imageProxy: ImageProxy ->
            if (imageProxy.image == null) {
                imageProxy.close()
                return@label
            }
            val rotationDegrees = imageProxy.imageInfo.rotationDegrees
            // converting from YUV format
            processor!!.detectInImage(imageProxy, toBitmap(imageProxy.image), rotationDegrees)
            // after done, release the ImageProxy object
            imageProxy.close()
        }
    }

    private fun toBitmap(image: Image?): Bitmap {
        val planes = image!!.planes
        val yBuffer = planes[0].buffer
        val uBuffer = planes[1].buffer
        val vBuffer = planes[2].buffer
        val ySize = yBuffer.remaining()
        val uSize = uBuffer.remaining()
        val vSize = vBuffer.remaining()
        val nv21 = ByteArray(ySize + uSize + vSize)
        //U and V are swapped
        yBuffer[nv21, 0, ySize]
        vBuffer[nv21, ySize, vSize]
        uBuffer[nv21, ySize + vSize, uSize]
        val yuvImage = YuvImage(nv21, ImageFormat.NV21, image.width, image.height, null)
        val out = ByteArrayOutputStream()
        yuvImage.compressToJpeg(Rect(0, 0, yuvImage.width, yuvImage.height), 75, out)
        val imageBytes = out.toByteArray()
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }

    private val lensFacing: Int
        get() = CameraSelector.LENS_FACING_BACK

    protected abstract fun setProcessor(): VisionBaseProcessor<*>?
    fun makeAddFaceVisible() {
        addFaceButton!!.visibility = View.VISIBLE
    }

    open fun onAddFaceClicked(view: View?) {}

    companion object {
        private const val REQUEST_CAMERA = 1001
    }
}
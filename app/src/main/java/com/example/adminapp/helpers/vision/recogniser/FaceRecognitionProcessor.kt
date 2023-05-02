package com.example.adminapp.helpers.vision.recogniser

import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.Rect
import android.media.Image
import android.text.Editable
import android.util.Log
import android.util.Pair
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageProxy
import com.example.adminapp.helpers.vision.FaceGraphic
import com.example.adminapp.helpers.vision.GraphicOverlay
import com.example.adminapp.helpers.vision.VisionBaseProcessor
import com.example.adminapp.network.AttendanceApi
import com.example.adminapp.`object`.FaceRecognitionActivity
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType
import okhttp3.RequestBody
import org.json.JSONObject
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import java.util.*
import kotlin.math.sqrt

class FaceRecognitionProcessor(
    private val faceNetModelInterpreter: Interpreter,
    private val graphicOverlay: GraphicOverlay,
    private val callback: FaceRecognitionCallback?
) : VisionBaseProcessor<List<Face?>?>() {
    private val detector: FaceDetector
    private val faceNetImageProcessor: ImageProcessor = ImageProcessor.Builder()
        .add(
            ResizeOp(
                FACENET_INPUT_IMAGE_SIZE,
                FACENET_INPUT_IMAGE_SIZE,
                ResizeOp.ResizeMethod.BILINEAR
            )
        )
        .add(NormalizeOp(0f, 255f))
        .build()
    var activity: FaceRecognitionActivity? = null
    var recognisedFaceList: MutableList<Person?> = ArrayList<Person?>()

    init {
        // initialize processors
        val faceDetectorOptions: FaceDetectorOptions = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE) // to ensure we don't count and analyse same person again
            .enableTracking()
            .build()
        detector = FaceDetection.getClient(faceDetectorOptions)
    }

    @OptIn(ExperimentalGetImage::class)
    override fun detectInImage(
        imageProxy: ImageProxy?,
        bitmap: Bitmap?,
        rotationDegrees: Int
    ): Task<List<Face?>?>? {
        val inputImage: InputImage =
            InputImage.fromMediaImage(imageProxy?.image as Image, rotationDegrees)

        // In order to correctly display the face bounds, the orientation of the analyzed
        // image and that of the viewfinder have to match. Which is why the dimensions of
        // the analyzed image are reversed if its rotation information is 90 or 270.
        val reverseDimens = rotationDegrees == 90 || rotationDegrees == 270
        val width: Int
        val height: Int
        if (reverseDimens) {
            width = imageProxy.height
            height = imageProxy.width
        } else {
            width = imageProxy.width
            height = imageProxy.height
        }
        return detector.process(inputImage)
            .addOnSuccessListener(object : OnSuccessListener<List<Face>> {
                override fun onSuccess(faces: List<Face>) {
                    graphicOverlay.clear()
                    for (face in faces) {
                        val faceGraphic = FaceGraphic(graphicOverlay, face, false, width, height)
                        Log.d(TAG, "face found, id: " + face.trackingId)
                        //                            if (activity != null) {
//                                activity.setTestImage(cropToBBox(bitmap, face.getBoundingBox(), rotation));
//                            }
                        // now we have a face, so we can use that to analyse age and gender
                        val faceBitmap: Bitmap? =
                            cropToBBox(bitmap as Bitmap, face.boundingBox, rotationDegrees)
                        if (faceBitmap == null) {
                            Log.d("GraphicOverlay", "Face bitmap null")
                            return
                        }
                        val tensorImage: TensorImage = TensorImage.fromBitmap(faceBitmap)
                        val faceNetByteBuffer = faceNetImageProcessor.process(tensorImage).buffer
                        val faceOutputArray = Array(1) { FloatArray(192) }
                        faceNetModelInterpreter.run(faceNetByteBuffer, faceOutputArray)
                        Log.d(TAG, "output array: " + faceOutputArray.contentDeepToString())
                        if (callback != null) {
                            callback.onFaceDetected(face, faceBitmap, faceOutputArray[0])
                            if (recognisedFaceList.isNotEmpty()) {
                                val result = findNearestFace(faceOutputArray[0])
                                // if distance is within confidence
                                if (result!!.second < 1.0f) {
                                    faceGraphic.name = result.first
                                    callback.onFaceRecognised(face, result.second, result.first)
                                }
                            }
                        }
                        graphicOverlay.add(faceGraphic)
                    }
                }
            })
            .addOnFailureListener {
                // intentionally left empty
            }
    }

    // looks for the nearest vector in the dataset (using L2 norm)
    // and returns the pair <name, distance>
    //distance is calculated
    private fun findNearestFace(vector: FloatArray): Pair<String, Float>? {
        var ret: Pair<String, Float>? = null
        for (person in recognisedFaceList) {
            val name = person!!.name
            val knownVector = person.faceVector
            var distance = 0f
            for (i in vector.indices) {
                val diff = vector[i] - knownVector[i]
                distance += diff * diff
            }
            distance = sqrt(distance.toDouble()).toFloat()
            if (ret == null || distance < ret.second) {
                ret = Pair(name, distance)
            }
        }
        return ret
    }

    override fun stop() {
        detector.close()
    }

    private fun cropToBBox(img: Bitmap, boundingBox: Rect, rotation: Int): Bitmap? {
        var image = img
        val shift = 0
        if (rotation != 0) {
            val matrix = Matrix()
            matrix.postRotate(rotation.toFloat())
            image =
                Bitmap.createBitmap(image, 0, 0, image.width, image.height, matrix, true)
        }
        return if (boundingBox.top >= 0 && boundingBox.bottom <= image.width && boundingBox.top + boundingBox.height() <= image.height && boundingBox.left >= 0 && boundingBox.left + boundingBox.width() <= image.width
        ) {
            Bitmap.createBitmap(
                image,
                boundingBox.left,
                boundingBox.top + shift,
                boundingBox.width(),
                boundingBox.height()
            )
        } else null
    }

    // Register a name against the vector
    fun registerFace(input: Editable, tempVector: FloatArray) {
        runBlocking {
            val jsonObject = JSONObject()
            jsonObject.put("adm_no", input.toString())
            jsonObject.put("face_data", tempVector.contentToString())
            val jsonObjectString = jsonObject.toString()
            val requestBody = RequestBody.create(
                MediaType.parse("application/json; charset=utf-8"),
                jsonObjectString
            )
            AttendanceApi.retrofitService.updateFace(requestBody)
        }
        recognisedFaceList.add(Person(input.toString(), tempVector))
    }

    interface FaceRecognitionCallback {
        fun onFaceRecognised(face: Face?, probability: Float, name: String?)
        fun onFaceDetected(face: Face?, faceBitmap: Bitmap?, vector: FloatArray?)
    }

    inner class Person(var name: String, var faceVector: FloatArray)
    companion object {
        private const val TAG = "FaceRecognitionProcessor"

        // Input image size for our facenet model
        private const val FACENET_INPUT_IMAGE_SIZE = 112
    }
}
package com.example.adminapp.`object`

import android.graphics.Bitmap
import android.os.Bundle
import com.example.adminapp.R
import com.example.adminapp.helpers.BoxWithText
import com.example.adminapp.helpers.MLImageHelperActivity
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions

class FaceDetectionActivity : MLImageHelperActivity() {
    private var faceDetector: FaceDetector? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // High-accuracy landmark detection and face classification
        val highAccuracyOpts = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
            .enableTracking()
            .build()
        faceDetector = FaceDetection.getClient(highAccuracyOpts)
    }

    override fun runDetection(bitmap: Bitmap?) {
        val finalBitmap = bitmap!!.copy(Bitmap.Config.ARGB_8888, true)
        val image = InputImage.fromBitmap(finalBitmap, 0)
        faceDetector!!.process(image)
            .addOnFailureListener { error: Exception -> error.printStackTrace() }
            .addOnSuccessListener { faces: List<Face> ->
                if (faces.isEmpty()) {
                    outputTextView!!.text = getString(R.string.no_faces_detected)
                } else {
                    outputTextView!!.text = String.format("%d faces detected", faces.size)
                    val boxes: MutableList<BoxWithText> = ArrayList<BoxWithText>()
                    for (face in faces) {
                        boxes.add(BoxWithText(face.trackingId.toString() + "", face.boundingBox))
                    }
                    inputImageView!!.setImageBitmap(
                        drawDetectionResult(
                            finalBitmap,
                            boxes as List<BoxWithText>
                        )
                    )
                }
            }
    }
}
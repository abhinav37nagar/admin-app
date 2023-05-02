package com.example.adminapp.image

import android.graphics.Bitmap
import android.os.Bundle
import com.example.adminapp.R
import com.example.adminapp.helpers.MLImageHelperActivity
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabel
import com.google.mlkit.vision.label.ImageLabeler
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions

class ImageClassificationActivity : MLImageHelperActivity() {
    private var imageLabeler: ImageLabeler? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imageLabeler = ImageLabeling.getClient(
            ImageLabelerOptions.Builder()
                .setConfidenceThreshold(0.7f)
                .build()
        )
    }

    override fun runDetection(bitmap: Bitmap?) {
        val inputImage = InputImage.fromBitmap(bitmap!!, 0)
        imageLabeler!!.process(inputImage).addOnSuccessListener { imageLabels: List<ImageLabel> ->
            val sb = StringBuilder()
            for (label in imageLabels) {
                sb.append(label.text).append(": ").append(label.confidence).append("\n")
            }
            if (imageLabels.isEmpty()) {
                outputTextView!!.text = getString(R.string.could_not_classify)
            } else {
                outputTextView!!.text = sb.toString()
            }
        }.addOnFailureListener { e: Exception -> e.printStackTrace() }
    }
}
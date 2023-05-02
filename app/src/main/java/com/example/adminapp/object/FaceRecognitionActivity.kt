package com.example.adminapp.`object`

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.adminapp.R
import com.example.adminapp.helpers.MLVideoHelperActivity
import com.example.adminapp.helpers.vision.GraphicOverlay
import com.example.adminapp.helpers.vision.VisionBaseProcessor
import com.example.adminapp.helpers.vision.recogniser.FaceRecognitionProcessor
import com.example.adminapp.helpers.vision.recogniser.FaceRecognitionProcessor.FaceRecognitionCallback
import com.google.mlkit.vision.face.Face
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import java.io.IOException

class FaceRecognitionActivity : MLVideoHelperActivity(), FaceRecognitionCallback {
    private var faceNetInterpreter: Interpreter? = null
    private var faceRecognitionProcessor: FaceRecognitionProcessor? = null
    private var face: Face? = null
    private var faceBitmap: Bitmap? = null
    private var faceVector: FloatArray? = floatArrayOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        makeAddFaceVisible()
    }

    override fun setProcessor(): VisionBaseProcessor<*> {
        try {
            faceNetInterpreter = Interpreter(
                FileUtil.loadMappedFile(this, "mobile_face_net.tflite"),
                Interpreter.Options()
            )
        } catch (e: IOException) {
            e.printStackTrace()
        }
        faceRecognitionProcessor = FaceRecognitionProcessor(
            faceNetInterpreter as Interpreter,
            graphicOverlay as GraphicOverlay,
            this
        )
        faceRecognitionProcessor!!.activity = this
        return faceRecognitionProcessor as FaceRecognitionProcessor
    }

    fun setTestImage(cropToBBox: Bitmap?) {
        if (cropToBBox == null) {
            return
        }
        runOnUiThread {
            (findViewById<View>(R.id.testImageView) as ImageView).setImageBitmap(
                cropToBBox
            )
        }
    }

    override fun onFaceDetected(face: Face?, faceBitmap: Bitmap?, vector: FloatArray?) {
        this.face = face
        this.faceBitmap = faceBitmap
        this.faceVector = vector
    }

    override fun onFaceRecognised(face: Face?, probability: Float, name: String?) {
        Toast.makeText(this@FaceRecognitionActivity, "marked", Toast.LENGTH_SHORT).show()
    }

    override fun onAddFaceClicked(view: View?) {
        super.onAddFaceClicked(view)
        if (face == null || faceBitmap == null) {
            return
        }
        val tempFace: Face = face as Face
        val tempBitmap: Bitmap = faceBitmap as Bitmap
        val tempVector = faceVector as FloatArray
        val inflater = LayoutInflater.from(this)
        val dialogView = inflater.inflate(R.layout.add_face_dialog, null)
        (dialogView.findViewById<View>(R.id.dlg_image) as ImageView).setImageBitmap(tempBitmap)
        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)
        builder.setPositiveButton("Save") { dialog, which ->
            val input = (dialogView.findViewById<View>(R.id.dlg_input) as EditText).editableText
            if (input.isNotEmpty()) {
                faceRecognitionProcessor!!.registerFace(input, tempVector)
            }
        }
        builder.show()
    }
}
package com.example.adminapp.helpers.vision

import android.graphics.Bitmap
import androidx.camera.core.ImageProxy
import com.google.android.gms.tasks.Task

abstract class VisionBaseProcessor<T> {
    abstract fun detectInImage(
        imageProxy: ImageProxy?,
        bitmap: Bitmap?,
        rotationDegrees: Int
    ): Task<T>?

    abstract fun stop()
}
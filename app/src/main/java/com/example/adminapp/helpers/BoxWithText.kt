package com.example.adminapp.helpers

import android.graphics.Rect
import android.graphics.RectF

class BoxWithText {
    @JvmField
    var text: String

    @JvmField
    var rect: Rect

    constructor(text: String, rect: Rect) {
        this.text = text
        this.rect = rect
    }

    constructor(displayName: String, boundingBox: RectF) {
        text = displayName
        rect = Rect()
        boundingBox.round(rect)
    }
}
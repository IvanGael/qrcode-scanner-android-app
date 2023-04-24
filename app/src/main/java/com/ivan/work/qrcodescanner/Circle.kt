package com.ivan.work.qrcodescanner

import android.graphics.Canvas
import android.graphics.Paint

class Circle(val x: Float, val y: Float, val radius: Float, private val paint: Paint) {
    fun draw(canvas: Canvas) {
        canvas.drawCircle(x, y, radius, paint)
    }
}

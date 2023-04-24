package com.ivan.work.qrcodescanner

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View

class OverlayView(context: Context) : View(context) {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.RED
        style = Paint.Style.FILL
    }
    private var circles: List<Circle> = emptyList()

    fun setCircles(circles: List<Circle>) {
        this.circles = circles
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for (circle in circles) {
            canvas.drawCircle(circle.x, circle.y, circle.radius, paint)
        }
    }
}

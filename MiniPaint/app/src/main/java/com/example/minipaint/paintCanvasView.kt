package com.example.minipaint

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import androidx.core.content.res.ResourcesCompat

class PaintCanvasView(context: Context) : View(context) {

    private var extraCanvas: Canvas? = null
    private var extraBitmap: Bitmap? = null
    private val backgroundColor = ResourcesCompat.getColor(resources, R.color.colorBackground, null)

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)
        extraBitmap?.run {
            this.recycle()
        }
        extraBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        extraBitmap?.run {
            extraCanvas = Canvas(this)
        }
        extraCanvas?.drawColor(backgroundColor)
    }

}
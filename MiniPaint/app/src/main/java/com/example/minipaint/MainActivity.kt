package com.example.minipaint

import android.os.Bundle
import android.os.PersistableBundle
import android.view.WindowInsets
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)

        val paintCanvasView = PaintCanvasView(this )

//        paintCanvasView.systemUiVisibility = SYSTEM_UI_FLAG_FULLSCREEN
        paintCanvasView.windowInsetsController?.hide(WindowInsets.Type.navigationBars())
        paintCanvasView.contentDescription = getString(R.string.canvasContentDescription)
        setContentView(paintCanvasView)

    }
}
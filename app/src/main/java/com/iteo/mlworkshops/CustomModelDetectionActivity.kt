package com.iteo.mlworkshops

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_custom_model.clearButton
import kotlinx.android.synthetic.main.activity_custom_model.paintView
import kotlinx.android.synthetic.main.activity_custom_model.resultText

private const val TAG = "CustomModelDetection"

class CustomModelDetectionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_model)
        paintView.drawingListener = { bitmap: Bitmap ->
            MnistDetector.classify(bitmap) { result ->
                resultText.text = result.toString()
            }
        }
        clearButton.setOnClickListener {
            paintView.clear()
            resultText.text = ""
        }
    }
}

fun createCustomModelDetectionIntent(context: Context) = Intent(context, CustomModelDetectionActivity::class.java)

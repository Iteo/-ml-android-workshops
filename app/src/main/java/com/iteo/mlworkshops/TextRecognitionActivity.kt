package com.iteo.mlworkshops

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import com.otaliastudios.cameraview.Frame
import kotlinx.android.synthetic.main.activity_text_recognition.camera
import kotlinx.android.synthetic.main.activity_text_recognition.text

private const val TAG = "TextRecognitionScreen"

class TextRecognitionActivity : AppCompatActivity() {

    var detectionInProgress = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_recognition)
        camera.setLifecycleOwner { this.lifecycle }
        camera.addFrameProcessor { frame: Frame ->
            if (detectionInProgress) {
                return@addFrameProcessor
            }

            detectionInProgress = true
            val metadata = FirebaseVisionImageMetadata.Builder()
                .setWidth(frame.size.width)
                .setHeight(frame.size.height)
                .setFormat(FirebaseVisionImageMetadata.IMAGE_FORMAT_NV21)
                .setRotation(frame.visionImageRotation())
                .build()

            val image = FirebaseVisionImage.fromByteArray(frame.data, metadata)
            val detector = FirebaseVision.getInstance().onDeviceTextRecognizer

            detector.processImage(image)
                .addOnSuccessListener { result ->
                    detectionInProgress = false
                    text.text = result.text
                    Log.d(TAG, result.text)
                }
                .addOnFailureListener { failure ->
                    Log.e(TAG, "Failure", failure)
                }
        }
    }
}

fun createTextRecognitionIntent(context: Context) = Intent(context, TextRecognitionActivity::class.java)

package com.iteo.mlworkshops

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_text_recognition.camera

private const val TAG = "FaceRecognitionScreen"

class FaceRecognitionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_face_recognition)
        camera.setLifecycleOwner { this.lifecycle }
    }
}

fun createFaceRecognitionIntent(context: Context) = Intent(context, FaceRecognitionActivity::class.java)

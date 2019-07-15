package com.iteo.mlworkshops

import android.Manifest
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.customModelButton
import kotlinx.android.synthetic.main.activity_main.faceLandmarksButton
import kotlinx.android.synthetic.main.activity_main.faceRecognitionButton
import kotlinx.android.synthetic.main.activity_main.imageLabelerButton
import kotlinx.android.synthetic.main.activity_main.textRecognitionButton
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions

@RuntimePermissions
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_Iteo_MlKit)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textRecognitionButton.setOnClickListener {
            openTextRecognitionWithPermissionCheck()
        }
        faceRecognitionButton.setOnClickListener {
            openFaceRecognitionWithPermissionCheck()
        }
        customModelButton.setOnClickListener {
            openCustomModelDetectionWithPermissionCheck()
        }
        imageLabelerButton.setOnClickListener {
            openImageLabelerWithPermissionCheck()
        }

        faceLandmarksButton.setOnClickListener {
            openFaceLandmarkRecognitionWithPermissionCheck()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }

    @NeedsPermission(Manifest.permission.CAMERA)
    fun openCustomModelDetection() {
        startActivity(createCustomModelDetectionIntent(this))
    }

    @NeedsPermission(Manifest.permission.CAMERA)
    fun openFaceLandmarkRecognition() {
        startActivity(createFaceLandmarksIntent(this))
    }

    @NeedsPermission(Manifest.permission.CAMERA)
    fun openImageLabeler() {
        startActivity(createImageLabeler(this))
    }

    @NeedsPermission(Manifest.permission.CAMERA)
    fun openTextRecognition() {
        startActivity(createTextRecognitionIntent(this))
    }

    @NeedsPermission(Manifest.permission.CAMERA)
    fun openFaceRecognition() {
        startActivity(createFaceRecognitionIntent(this))
    }
}

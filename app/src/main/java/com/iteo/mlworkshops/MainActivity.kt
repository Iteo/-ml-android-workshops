package com.iteo.mlworkshops

import android.Manifest
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
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
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }

    @NeedsPermission(Manifest.permission.CAMERA)
    fun openTextRecognition() {
        startActivity(createTextRecognitionIntent(this))
    }
}

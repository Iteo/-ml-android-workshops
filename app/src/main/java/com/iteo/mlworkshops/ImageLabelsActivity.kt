package com.iteo.mlworkshops

import android.content.Context
import android.content.Intent
import android.hardware.camera2.CameraCharacteristics
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.activity_camera_with_overlay.camera
import kotlinx.android.synthetic.main.activity_camera_with_overlay.overlay
import kotlinx.android.synthetic.main.activity_camera_with_overlay.text

class ImageLabelsActivity : AppCompatActivity() {

    private var labelerDisposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera_with_overlay)

        camera.setLifecycleOwner(this)
        labelerDisposable = camera.frameRecognitionStream()
            .doOnNext { (_, metadata) ->
                overlay.setCameraInfo(metadata.realWidth(), metadata.realHeight(), CameraCharacteristics.LENS_FACING_BACK)
            }.concatMapSingle { (image, _) ->
                processLabeler(image)
            }.subscribeBy(
                onNext = { results -> text.setText(results.map { it.text }.joinToString()) }
            )
    }

    override fun onDestroy() {
        labelerDisposable?.dispose()
        super.onDestroy()
    }

    fun processLabeler(image: FirebaseVisionImage) = Single.create<List<FirebaseVisionImageLabel>> { emitter ->
        val labeler = FirebaseVision.getInstance().onDeviceImageLabeler
        labeler.processImage(image)
            .addOnSuccessListener { result ->
                emitter.onSuccess(result)
            }
            .addOnFailureListener { error ->
                emitter.tryOnError(error)
            }
    }
}

fun createImageLabeler(context: Context) = Intent(context, ImageLabelsActivity::class.java)

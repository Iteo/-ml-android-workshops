package com.iteo.mlworkshops

import android.content.Context
import android.content.Intent
import android.hardware.camera2.CameraCharacteristics
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import com.google.firebase.ml.vision.text.FirebaseVisionText
import com.otaliastudios.cameraview.Frame
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_camera_with_overlay.camera
import kotlinx.android.synthetic.main.activity_camera_with_overlay.overlay
import kotlinx.android.synthetic.main.activity_camera_with_overlay.text

private const val TAG = "TextRecognitionScreen"

class TextRecognitionActivity : AppCompatActivity() {

    var detectionInProgress = false
    var recognitionDisposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera_with_overlay)
        camera.setLifecycleOwner { this.lifecycle }
//        setupFrameProcessor()

        setupRxTextRecognition()
    }

    override fun onDestroy() {
        recognitionDisposable?.dispose()
        super.onDestroy()
    }

    private fun setupFrameProcessor() {
        camera.addFrameProcessor { frame: Frame ->
            if (detectionInProgress) {
                return@addFrameProcessor
            }

            detectionInProgress = true
            val metadata = frame.visionMetadata()

            val image = FirebaseVisionImage.fromByteArray(frame.data, metadata)
            val detector = FirebaseVision.getInstance().onDeviceTextRecognizer

            updateOverlayParameters(metadata)

            detector.processImage(image)
                .addOnSuccessListener { result ->
                    postTextResults(result)
                    detectionInProgress = false
                }
                .addOnFailureListener { failure ->
                    Log.e(TAG, "Failure", failure)
                }
        }
    }

    private fun setupRxTextRecognition() {
        recognitionDisposable = camera.frameRecognitionStream()
            .doOnNext { (_, metadata) -> updateOverlayParameters(metadata) }
            .concatMap { (image, _) ->
                val detector = FirebaseVision.getInstance().onDeviceTextRecognizer
                return@concatMap detector.computeResults(image).toFlowable()
            }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = { result ->
                    postTextResults(result)
                },
                onError = { error ->
                    Log.e(TAG, "error", error)
                }
            )
    }

    private fun updateOverlayParameters(metadata: FirebaseVisionImageMetadata) {
        if (overlay.previewWidth != metadata.realWidth() || overlay.previewHeight != metadata.realHeight()) {
            overlay.setCameraInfo(
                metadata.realWidth(),
                metadata.realHeight(),
                CameraCharacteristics.LENS_FACING_BACK)
        }
    }

    private fun postTextResults(result: FirebaseVisionText) {
        overlay.clear()
        result.textBlocks.forEach { block ->
            block.lines.forEach { line ->
                line.elements.forEach { lineElement ->
                    overlay.add(TextGraphic(overlay, lineElement))
                }
            }
        }
        text.text = result.text
    }
}

fun createTextRecognitionIntent(context: Context) = Intent(context, TextRecognitionActivity::class.java)

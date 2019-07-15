package com.iteo.mlworkshops

import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata.Builder
import com.google.firebase.ml.vision.text.FirebaseVisionText
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer
import com.otaliastudios.cameraview.CameraView
import com.otaliastudios.cameraview.Frame
import com.otaliastudios.cameraview.FrameProcessor
import io.reactivex.BackpressureStrategy.LATEST
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.Flowables
import java.util.concurrent.CancellationException

fun Frame.visionImageRotation() = when (this.rotation) {
    90 -> FirebaseVisionImageMetadata.ROTATION_90
    180 -> FirebaseVisionImageMetadata.ROTATION_180
    270 -> FirebaseVisionImageMetadata.ROTATION_270
    else -> FirebaseVisionImageMetadata.ROTATION_0
}

fun Frame.visionMetadata() = Builder()
    .setWidth(size.width)
    .setHeight(size.height)
    .setFormat(FirebaseVisionImageMetadata.IMAGE_FORMAT_NV21)
    .setRotation(visionImageRotation())
    .build()

fun Frame.realHeight() = when (this.rotation) {
    90 -> this.size.width
    180 -> this.size.height
    270 -> this.size.width
    else -> this.size.height
}

fun Frame.realWidth() = when (this.rotation) {
    90 -> this.size.height
    180 -> this.size.width
    270 -> this.size.height
    else -> this.size.width
}

fun FirebaseVisionImageMetadata.realHeight() = when (this.rotation) {
    FirebaseVisionImageMetadata.ROTATION_90 -> this.width
    FirebaseVisionImageMetadata.ROTATION_180 -> this.height
    FirebaseVisionImageMetadata.ROTATION_270 -> this.width
    else -> this.height
}

fun FirebaseVisionImageMetadata.realWidth() = when (this.rotation) {
    FirebaseVisionImageMetadata.ROTATION_90 -> this.height
    FirebaseVisionImageMetadata.ROTATION_180 -> this.width
    FirebaseVisionImageMetadata.ROTATION_270 -> this.height
    else -> this.width
}

fun CameraView.frameRecognitionStream(): Flowable<Pair<FirebaseVisionImage, FirebaseVisionImageMetadata>> {
    return Flowables.create<Pair<FirebaseVisionImage, FirebaseVisionImageMetadata>>(LATEST) { emitter ->
        var isProcessing = false

        val processor = FrameProcessor { frame ->
            if(isProcessing) {
                return@FrameProcessor
            }
            isProcessing = true

            val metadata = frame.visionMetadata()
            val image = FirebaseVisionImage.fromByteArray(frame.data, metadata)
            emitter.onNext(image to metadata)
            isProcessing = false
        }
        addFrameProcessor(processor)

        emitter.setDisposable(object : Disposable {

            var disposed = false
            override fun isDisposed() = disposed

            override fun dispose() {
                removeFrameProcessor(processor)
                disposed = true
            }
        })
    }
}

fun FirebaseVisionTextRecognizer.computeResults(image: FirebaseVisionImage): Single<FirebaseVisionText> {
    return Single.create { emitter ->
        processImage(image)
            .addOnSuccessListener { result -> emitter.onSuccess(result) }
            .addOnFailureListener { error -> emitter.tryOnError(error) }
            .addOnCanceledListener { emitter.tryOnError(CancellationException()) }
    }
}

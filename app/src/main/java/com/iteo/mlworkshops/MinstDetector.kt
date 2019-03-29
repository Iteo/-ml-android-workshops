package com.iteo.mlworkshops

import android.graphics.Bitmap
import android.graphics.Color
import com.google.firebase.ml.custom.FirebaseModelOutputs

object MinstDetector {
    private val classCount = 10
    private val imageWidth = 28
    private val imageHeight = 28
    private val imageSize = imageWidth * imageHeight
    private val imagePixels = IntArray(imageSize)


    fun classify(
        bitmap: Bitmap,
        success: (Int) -> Unit
    ) {

    }

    private fun Bitmap.toVector(): Array<Array<Array<FloatArray>>> {
        getPixels(imagePixels, 0, width, 0, 0, width, height)
        return Array(1) {
            Array(imageHeight) { y ->
                Array(imageWidth) { x ->
                    floatArrayOf(imagePixels[x + (y * imageWidth)].convertToGreyScale())
                }
            }
        }
    }

    private fun Int.convertToGreyScale(): Float =
        1f - ((Color.red(this) + Color.green(this) + Color.blue(this)).toFloat() / 3f / 255f)

    private fun FirebaseModelOutputs.map(): Map<Int, Float> {
        return getOutput<Array<FloatArray>>(0)[0].mapIndexed { index, fl -> index to fl }.toMap()
    }
}

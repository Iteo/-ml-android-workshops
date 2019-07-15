package com.iteo.mlworkshops

import com.google.firebase.ml.common.modeldownload.FirebaseLocalModel
import com.google.firebase.ml.common.modeldownload.FirebaseModelManager
import com.google.firebase.ml.custom.FirebaseModelInterpreter
import com.google.firebase.ml.custom.FirebaseModelOptions

object CustomModelInterpreter {

    private val options: FirebaseModelOptions

    init {

        val localSource = FirebaseLocalModel.Builder("mnist") // Assign a name to this model
            .setAssetFilePath("mnist.tflite")
            .build()
        FirebaseModelManager.getInstance().registerLocalModel(localSource)


        options = FirebaseModelOptions.Builder()
            .setLocalModelName("mnist")
            .build()
    }

    fun getInterpreter(): FirebaseModelInterpreter? {
        return FirebaseModelInterpreter.getInstance(options)
    }
}

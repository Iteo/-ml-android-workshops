package com.iteo.mlworkshops

import android.app.Application
import android.util.Log

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        Thread.setDefaultUncaughtExceptionHandler { t, e ->
            Log.e("ERRor", "Global error", e)
        }
    }
}

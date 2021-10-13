package com.cc.localcrashreporter

import android.app.Application
import com.library.CrashReporter

class LocalCrashReporter : Application() {

    override fun onCreate() {
        super.onCreate()
        BuildConfig.APPLICATION_ID
        CrashReporter.initialize(this, cacheDir.absolutePath)
    }

}
package com.shamiacademy.maktabalibrary

import android.app.Application
import com.shamiacademy.maktabalibrary.util.CrashHandler

class MaktabaApp : Application() {
    override fun onCreate() {
        super.onCreate()
        CrashHandler.install(this)
    }
}

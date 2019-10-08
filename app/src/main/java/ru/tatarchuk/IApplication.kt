package ru.tatarchuk

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen

class IApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
    }
}
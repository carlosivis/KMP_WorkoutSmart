package dev.carlosivis.workoutsmart.android

import android.app.Application
import dev.carlosivis.workoutsmart.di.commonModule
import dev.carlosivis.workoutsmart.di.platformModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MainApplication)
            modules(commonModule, platformModule())
        }
    }
}

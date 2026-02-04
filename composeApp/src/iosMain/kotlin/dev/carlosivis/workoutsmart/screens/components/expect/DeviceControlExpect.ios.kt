package dev.carlosivis.workoutsmart.screens.components.expect

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import platform.AudioToolbox.AudioServicesPlaySystemSound
import platform.AudioToolbox.kSystemSoundID_Vibrate
import platform.UIKit.UIApplication

class IosVibratorHelper : VibratorHelper {
    override fun vibrate(milliseconds: Long) {
        AudioServicesPlaySystemSound(kSystemSoundID_Vibrate)
    }
}

@Composable
actual fun KeepScreenOn(enabled: Boolean) {
    DisposableEffect(enabled) {
        val application = UIApplication.sharedApplication
        val previousState = application.isIdleTimerDisabled()

        if (enabled) {
            application.idleTimerDisabled = true
        }

        onDispose {
            application.idleTimerDisabled = previousState
        }
    }
}
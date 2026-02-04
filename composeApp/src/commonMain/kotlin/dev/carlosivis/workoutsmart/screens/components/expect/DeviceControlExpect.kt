package dev.carlosivis.workoutsmart.screens.components.expect

import androidx.compose.runtime.Composable

interface VibratorHelper {
    fun vibrate(milliseconds: Long = 500)
}

@Composable
expect fun KeepScreenOn(enabled: Boolean)
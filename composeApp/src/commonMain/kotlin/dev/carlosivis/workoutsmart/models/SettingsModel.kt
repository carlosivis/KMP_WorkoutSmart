package dev.carlosivis.workoutsmart.models

import dev.carlosivis.workoutsmart.utils.ThemeMode

data class SettingsModel(
    val themeMode: ThemeMode,
    val defaultRestSeconds: Int,
    val keepScreenOn: Boolean,
    val vibrationEnabled: Boolean
){
    companion object{
        fun default() = SettingsModel(
            themeMode = ThemeMode.SYSTEM,
            defaultRestSeconds = 60,
            keepScreenOn = false,
            vibrationEnabled = true
        )
    }
}

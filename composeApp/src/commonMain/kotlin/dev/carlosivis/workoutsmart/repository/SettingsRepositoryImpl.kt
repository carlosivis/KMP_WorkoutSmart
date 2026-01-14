package dev.carlosivis.workoutsmart.repository

import com.russhwolf.settings.Settings
import dev.carlosivis.workoutsmart.models.SettingsModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsRepositoryImpl(private val settings: Settings): SettingsRepository {

    private val _settingsFlow = MutableStateFlow(readFromStorage())

    private fun readFromStorage(): SettingsModel {
        val themeOrdinal = settings.getInt(KEY_THEME, ThemeMode.SYSTEM.ordinal)
        val theme = ThemeMode.entries.getOrElse(themeOrdinal) { ThemeMode.SYSTEM }

        return SettingsModel(
            themeMode = theme,
            defaultRestSeconds = settings.getInt(KEY_REST_TIME, 60),
            keepScreenOn = settings.getBoolean(KEY_KEEP_SCREEN_ON, false),
            vibrationEnabled = settings.getBoolean(KEY_VIBRATION, true)
        )
    }

    override suspend fun getSettings(): Flow<SettingsModel> =
        _settingsFlow.asStateFlow()


    override suspend fun saveSettings(newSettings: SettingsModel) {
        settings.putInt(KEY_THEME, newSettings.themeMode.ordinal)
        settings.putInt(KEY_REST_TIME, newSettings.defaultRestSeconds)
        settings.putBoolean(KEY_KEEP_SCREEN_ON, newSettings.keepScreenOn)
        settings.putBoolean(KEY_VIBRATION, newSettings.vibrationEnabled)

        _settingsFlow.value = newSettings
    }


    companion object {
        private const val KEY_THEME = "pref_theme"
        private const val KEY_REST_TIME = "pref_rest_time"
        private const val KEY_KEEP_SCREEN_ON = "pref_keep_screen_on"
        private const val KEY_VIBRATION = "pref_vibration"
    }
}

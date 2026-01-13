package dev.carlosivis.workoutsmart.repository

import com.russhwolf.settings.Settings
import dev.carlosivis.workoutsmart.models.SettingsModel
import kotlinx.coroutines.flow.Flow

class SettingsRepositoryImpl(private val settings: Settings): SettingsRepository {
    override var themeMode: ThemeMode
        get() {
            val ordinal = settings.getInt(KEY_THEME, ThemeMode.SYSTEM.ordinal)
            return ThemeMode.entries.toTypedArray().getOrElse(ordinal) { ThemeMode.SYSTEM }
        }
        set(value) {
            settings.putInt(KEY_THEME, value.ordinal)
        }

    override var defaultRestSeconds: Int
        get() = settings.getInt(KEY_REST_TIME, 60)
        set(value) = settings.putInt(KEY_REST_TIME, value)

    override var keepScreenOn: Boolean
        get() = settings.getBoolean(KEY_KEEP_SCREEN_ON, false)
        set(value) = settings.putBoolean(KEY_KEEP_SCREEN_ON, value)

    override var vibrationEnabled: Boolean
        get() = settings.getBoolean(KEY_VIBRATION, true)
        set(value) = settings.putBoolean(KEY_VIBRATION, value)

    override suspend fun getSettings(): Flow<SettingsModel> {
        TODO("Not yet implemented")
    }

    override suspend fun saveSettings(settings: SettingsModel) {
        this.themeMode = settings.themeMode
        this.defaultRestSeconds = settings.defaultRestSeconds
        this.keepScreenOn = settings.keepScreenOn
        this.vibrationEnabled = settings.vibrationEnabled
    }


    companion object {
        private const val KEY_THEME = "pref_theme"
        private const val KEY_REST_TIME = "pref_rest_time"
        private const val KEY_KEEP_SCREEN_ON = "pref_keep_screen_on"
        private const val KEY_VIBRATION = "pref_vibration"
    }
}

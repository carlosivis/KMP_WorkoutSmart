package dev.carlosivis.workoutsmart.data.local.datasource

import com.russhwolf.settings.Settings

class SettingsLocalDataSourceImpl(
    private val settings: Settings
) : SettingsLocalDataSource {

    private companion object {
        const val KEY_THEME = "pref_theme"
        const val KEY_REST_TIME = "pref_rest_time"
        const val KEY_KEEP_SCREEN_ON = "pref_keep_screen_on"
        const val KEY_VIBRATION = "pref_vibration"
    }

    override fun getThemeOrdinal(): Int {
        return settings.getInt(KEY_THEME, -1)
    }

    override fun getRestTime(): Int {
        return settings.getInt(KEY_REST_TIME, 60)
    }

    override fun isScreenOnEnabled(): Boolean {
        return settings.getBoolean(KEY_KEEP_SCREEN_ON, false)
    }

    override fun isVibrationEnabled(): Boolean {
        return settings.getBoolean(KEY_VIBRATION, true)
    }

    override fun saveThemeOrdinal(ordinal: Int) {
        settings.putInt(KEY_THEME, ordinal)
    }

    override fun saveRestTime(seconds: Int) {
        settings.putInt(KEY_REST_TIME, seconds)
    }

    override fun saveScreenOn(isEnabled: Boolean) {
        settings.putBoolean(KEY_KEEP_SCREEN_ON, isEnabled)
    }

    override fun saveVibration(isEnabled: Boolean) {
        settings.putBoolean(KEY_VIBRATION, isEnabled)
    }
}
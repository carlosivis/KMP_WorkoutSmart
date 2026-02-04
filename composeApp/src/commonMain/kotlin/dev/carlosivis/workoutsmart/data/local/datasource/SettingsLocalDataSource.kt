package dev.carlosivis.workoutsmart.data.local.datasource

interface SettingsLocalDataSource {
    fun getThemeOrdinal(): Int
    fun getRestTime(): Int
    fun isScreenOnEnabled(): Boolean
    fun isVibrationEnabled(): Boolean

    fun saveThemeOrdinal(ordinal: Int)
    fun saveRestTime(seconds: Int)
    fun saveScreenOn(isEnabled: Boolean)
    fun saveVibration(isEnabled: Boolean)
}
package dev.carlosivis.workoutsmart.repository

import dev.carlosivis.workoutsmart.data.local.datasource.SettingsLocalDataSource
import dev.carlosivis.workoutsmart.models.SettingsModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsRepositoryImpl(
    private val dataSource: SettingsLocalDataSource
) : SettingsRepository {

    private val _settingsFlow = MutableStateFlow(readFromDataSource())

    private fun readFromDataSource(): SettingsModel {
        val themeOrdinal = dataSource.getThemeOrdinal()

        val theme = if (themeOrdinal == -1) {
            ThemeMode.SYSTEM
        } else {
            ThemeMode.entries.getOrElse(themeOrdinal) { ThemeMode.SYSTEM }
        }

        return SettingsModel(
            themeMode = theme,
            defaultRestSeconds = dataSource.getRestTime(),
            keepScreenOn = dataSource.isScreenOnEnabled(),
            vibrationEnabled = dataSource.isVibrationEnabled()
        )
    }

    override fun getSettings(): Flow<SettingsModel> =
        _settingsFlow.asStateFlow()

    override suspend fun saveSettings(newSettings: SettingsModel) {
        dataSource.apply {
            saveThemeOrdinal(newSettings.themeMode.ordinal)
            saveRestTime(newSettings.defaultRestSeconds)
            saveScreenOn(newSettings.keepScreenOn)
            saveVibration(newSettings.vibrationEnabled)
        }

        _settingsFlow.value = newSettings
    }
}

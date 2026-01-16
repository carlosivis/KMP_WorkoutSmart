package dev.carlosivis.workoutsmart.repository

import dev.carlosivis.workoutsmart.models.SettingsModel
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun getSettings(): Flow<SettingsModel>
    suspend fun saveSettings(settings: SettingsModel)
}

enum class ThemeMode{
    LIGHT,
    DARK,
    SYSTEM
}

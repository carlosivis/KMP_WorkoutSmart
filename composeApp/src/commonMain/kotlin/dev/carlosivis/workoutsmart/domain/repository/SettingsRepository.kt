package dev.carlosivis.workoutsmart.domain.repository

import dev.carlosivis.workoutsmart.models.SettingsModel
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun getSettings(): Flow<SettingsModel>
    suspend fun saveSettings(settings: SettingsModel)
}


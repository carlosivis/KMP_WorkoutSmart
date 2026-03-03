package dev.carlosivis.workoutsmart.data.local.datasource

import dev.carlosivis.workoutsmart.models.HistoryModel
import kotlinx.coroutines.flow.Flow

interface HistoryLocalDataSource {
    fun getAllHistory(): Flow<List<HistoryModel>>
    suspend fun insertHistory(workoutName: String, date: Long, duration: Long)
}
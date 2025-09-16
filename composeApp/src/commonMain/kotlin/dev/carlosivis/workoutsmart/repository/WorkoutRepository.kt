package dev.carlosivis.workoutsmart.repository

import dev.carlosivis.workoutsmart.models.HistoryModel
import dev.carlosivis.workoutsmart.models.WorkoutModel
import kotlinx.coroutines.flow.Flow

interface WorkoutRepository {
    fun getAllWorkouts(): Flow<List<WorkoutModel>>
    fun getAllHistory(): Flow<List<HistoryModel>>
    suspend fun insertWorkout(workout: WorkoutModel)
    suspend fun deleteWorkout(workoutId: Long)
    suspend fun insertHistory(workoutName: String, date: Long)
}


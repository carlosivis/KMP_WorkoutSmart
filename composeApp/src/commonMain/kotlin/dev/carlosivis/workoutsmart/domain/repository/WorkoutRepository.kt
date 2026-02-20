package dev.carlosivis.workoutsmart.domain.repository

import dev.carlosivis.workoutsmart.models.HistoryModel
import dev.carlosivis.workoutsmart.models.WorkoutModel
import dev.carlosivis.workoutsmart.models.WorkoutSummaryModel
import kotlinx.coroutines.flow.Flow

interface WorkoutRepository {
    fun getAllWorkouts(): Flow<List<WorkoutSummaryModel>>
    fun getAllHistory(): Flow<List<HistoryModel>>
    fun getWorkoutById(id: Long): Flow<WorkoutModel>
    suspend fun insertWorkout(workout: WorkoutModel)
    suspend fun updateWorkout(workout: WorkoutModel)
    suspend fun deleteWorkout(workoutId: Long)
    suspend fun insertHistory(workoutName: String, timestamp: Long, duration: Long)
}

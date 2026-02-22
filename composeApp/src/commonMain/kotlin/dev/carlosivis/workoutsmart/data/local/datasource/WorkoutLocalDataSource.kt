package dev.carlosivis.workoutsmart.data.local.datasource

import dev.carlosivis.workoutsmart.models.WorkoutModel
import dev.carlosivis.workoutsmart.models.WorkoutSummaryModel
import kotlinx.coroutines.flow.Flow

interface WorkoutLocalDataSource {
    suspend fun insertWorkout(workout: WorkoutModel)
    suspend fun updateWorkout(workout: WorkoutModel)
    suspend fun deleteWorkout(workoutId: Long)
    fun getAllWorkouts(): Flow<List<WorkoutSummaryModel>>
    fun getWorkoutById(id: Long): Flow<WorkoutModel>
}
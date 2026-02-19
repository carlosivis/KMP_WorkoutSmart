package dev.carlosivis.workoutsmart.repository

import dev.carlosivis.workoutsmart.data.local.DatabaseHelper
import dev.carlosivis.workoutsmart.models.HistoryModel
import dev.carlosivis.workoutsmart.models.WorkoutModel
import dev.carlosivis.workoutsmart.models.WorkoutSummaryModel
import kotlinx.coroutines.flow.Flow

class WorkoutRepositoryImpl(
    private val databaseHelper: DatabaseHelper
) : WorkoutRepository {
    override fun getAllWorkouts(): Flow<List<WorkoutSummaryModel>> =
        databaseHelper.getAllWorkouts()

    override fun getAllHistory(): Flow<List<HistoryModel>> =
        databaseHelper.getAllHistory()

    override fun getWorkoutById(id: Long): Flow<WorkoutModel> =
        databaseHelper.getWorkoutById(id)

    override suspend fun insertWorkout(workout: WorkoutModel) =
        databaseHelper.insertWorkout(workout)

    override suspend fun updateWorkout(workout: WorkoutModel) =
        databaseHelper.updateWorkout(workout)

    override suspend fun deleteWorkout(workoutId: Long) =
        databaseHelper.deleteWorkout(workoutId)

    override suspend fun insertHistory(workoutName: String, timestamp: Long, duration: Long) =
        databaseHelper.insertHistory(workoutName, timestamp, duration)
}


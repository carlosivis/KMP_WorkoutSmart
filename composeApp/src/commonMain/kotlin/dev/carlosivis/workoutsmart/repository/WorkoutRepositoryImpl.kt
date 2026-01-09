package dev.carlosivis.workoutsmart.repository

import dev.carlosivis.workoutsmart.database.DatabaseHelper
import dev.carlosivis.workoutsmart.models.HistoryModel
import dev.carlosivis.workoutsmart.models.WorkoutModel
import kotlinx.coroutines.flow.Flow

class WorkoutRepositoryImpl(
    private val databaseHelper: DatabaseHelper
) : WorkoutRepository {
    override fun getAllWorkouts(): Flow<List<WorkoutModel>> =
        databaseHelper.getAllWorkouts()

    override fun getAllHistory(): Flow<List<HistoryModel>> =
        databaseHelper.getAllHistory()

    override suspend fun insertWorkout(workout: WorkoutModel) =
        databaseHelper.insertWorkout(workout)

    override suspend fun updateWorkout(workout: WorkoutModel) =
        databaseHelper.updateWorkout(workout)

    override suspend fun deleteWorkout(workoutId: Long) =
        databaseHelper.deleteWorkout(workoutId)

    override suspend fun insertHistory(workoutName: String, date: Long, duration: Long) =
        databaseHelper.insertHistory(workoutName, date, duration)
}


package dev.carlosivis.workoutsmart.data.repository

import dev.carlosivis.workoutsmart.data.local.datasource.HistoryLocalDataSource
import dev.carlosivis.workoutsmart.data.local.datasource.WorkoutLocalDataSource
import dev.carlosivis.workoutsmart.domain.repository.WorkoutRepository
import dev.carlosivis.workoutsmart.models.HistoryModel
import dev.carlosivis.workoutsmart.models.WorkoutModel
import dev.carlosivis.workoutsmart.models.WorkoutSummaryModel
import kotlinx.coroutines.flow.Flow

class WorkoutRepositoryImpl(
    private val workoutLocalDataSource: WorkoutLocalDataSource,
    private val historyLocalDataSource: HistoryLocalDataSource
) : WorkoutRepository {
    override fun getAllWorkouts(): Flow<List<WorkoutSummaryModel>> =
        workoutLocalDataSource.getAllWorkouts()

    override fun getAllHistory(): Flow<List<HistoryModel>> =
        historyLocalDataSource.getAllHistory()

    override fun getWorkoutById(id: Long): Flow<WorkoutModel> =
        workoutLocalDataSource.getWorkoutById(id)

    override suspend fun insertWorkout(workout: WorkoutModel) =
        workoutLocalDataSource.insertWorkout(workout)

    override suspend fun updateWorkout(workout: WorkoutModel) =
        workoutLocalDataSource.updateWorkout(workout)

    override suspend fun deleteWorkout(workoutId: Long) =
        workoutLocalDataSource.deleteWorkout(workoutId)

    override suspend fun insertHistory(workoutName: String, timestamp: Long, duration: Long) =
        historyLocalDataSource.insertHistory(workoutName, timestamp, duration)
}


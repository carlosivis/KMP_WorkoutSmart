package dev.carlosivis.workoutsmart.database

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.db.SqlDriver
import dev.carlosivis.workoutsmart.Utils.transactionWithContext
import dev.carlosivis.workoutsmart.models.ExerciseModel
import dev.carlosivis.workoutsmart.models.HistoryModel
import dev.carlosivis.workoutsmart.models.WorkoutModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class DatabaseHelper(
    sqlDriver: SqlDriver,
    private val backgroundDispatcher: CoroutineDispatcher
) {
    private val dbRef: WorkoutSmartDatabase = WorkoutSmartDatabase(sqlDriver)

    // Workout Operations
    fun getAllWorkouts(): Flow<List<WorkoutModel>> = dbRef.workoutSmartDatabaseQueries
        .selectAllWorkouts()
        .asFlow()
        .mapToList(Dispatchers.Default)
        .map { workouts ->
            workouts.map { workout ->
                WorkoutModel(
                    id = workout.id,
                    name = workout.name,
                    description = workout.description,
                    exercises = getExercisesForWorkoutSync(workout.id)
                )
            }
        }
        .flowOn(backgroundDispatcher)

    suspend fun insertWorkout(workout: WorkoutModel) {
        dbRef.transactionWithContext(backgroundDispatcher) {
            dbRef.workoutSmartDatabaseQueries.insertWorkout(
                id = workout.id,
                name = workout.name,
                description = workout.description
            )
            workout.exercises.forEach { exercise ->
               suspend { insertExercise(workout.id, exercise)}
            }
        }
    }

    suspend fun deleteWorkout(workoutId: String) {
        dbRef.transactionWithContext(backgroundDispatcher) {
            dbRef.workoutSmartDatabaseQueries.deleteWorkout(workoutId)
        }
    }

    // Exercise Operations
    private suspend fun insertExercise(workoutId: String, exercise: ExerciseModel) {
        dbRef.transactionWithContext(backgroundDispatcher) {
            dbRef.workoutSmartDatabaseQueries.insertExercise(
                workoutId = workoutId,
                name = exercise.name,
                notes = exercise.notes,
                series = exercise.series.toLong(),
                repetitions = exercise.repetitions.toLong(),
                videoUrl = exercise.videoUrl,
                imageUrl = exercise.imageUrl
            )
        }
    }

    private fun getExercisesForWorkoutSync(workoutId: String): List<ExerciseModel> =
        dbRef.workoutSmartDatabaseQueries
            .selectExercisesByWorkoutId(workoutId)
            .executeAsList()
            .map { exercise ->
                ExerciseModel(
                    id = exercise.id.toInt(),
                    name = exercise.name,
                    notes = exercise.notes,
                    series = exercise.series.toInt(),
                    repetitions = exercise.repetitions.toInt(),
                    videoUrl = exercise.videoUrl,
                    imageUrl = exercise.imageUrl
                )
            }

    // History Operations
    fun getAllHistory(): Flow<List<HistoryModel>> = dbRef.workoutSmartDatabaseQueries
        .selectAllHistory()
        .asFlow()
        .mapToList(Dispatchers.Default)
        .map { historyEntries ->
            historyEntries.map { history ->
                HistoryModel(
                    id = history.id.toLong(),
                    workoutName = history.workoutName,
                    date = history.date
                )
            }
        }
        .flowOn(backgroundDispatcher)

    suspend fun insertHistory(workoutName: String, date: String) {
        dbRef.transactionWithContext(backgroundDispatcher) {
            dbRef.workoutSmartDatabaseQueries.insertHistory(
                workoutName = workoutName,
                date = date
            )
        }
    }
}

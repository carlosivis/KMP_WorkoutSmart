package dev.carlosivis.workoutsmart.database

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.db.SqlDriver
import dev.carlosivis.workoutsmart.core.transactionWithContext
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

    suspend fun insertWorkout(workout: WorkoutModel) {
        dbRef.transactionWithContext(backgroundDispatcher) {
            dbRef.workoutSmartDatabaseQueries.insertWorkout(
                name = workout.name,
                description = workout.description.ifEmpty { "Sem descrição" }
            )
            val workoutId: Long =
                dbRef.workoutSmartDatabaseQueries.selectLastInsertedWorkoutId().executeAsOne()

            workout.exercises.forEach { exercise ->
                dbRef.workoutSmartDatabaseQueries.insertExercise(
                    workoutId = workoutId,
                    name = exercise.name,
                    notes = exercise.notes,
                    series = exercise.series.toLong(),
                    repetitions = exercise.repetitions.toLong(),
                    image = exercise.image
                )
            }
        }
    }

    suspend fun updateWorkout(workout: WorkoutModel) {
        dbRef.transactionWithContext(backgroundDispatcher) {
            // Atualizar informações do treino
            dbRef.workoutSmartDatabaseQueries.updateWorkout(
                name = workout.name,
                description = workout.description.ifEmpty { "Sem descrição" },
                id = workout.id
            )

            dbRef.workoutSmartDatabaseQueries.deleteExercisesFromWorkout(workout.id)

            workout.exercises.forEach { exercise ->
                dbRef.workoutSmartDatabaseQueries.insertExercise(
                    workoutId = workout.id,
                    name = exercise.name,
                    notes = exercise.notes,
                    series = exercise.series.toLong(),
                    repetitions = exercise.repetitions.toLong(),
                    image = exercise.image
                )
            }
        }
    }

    suspend fun deleteWorkout(workoutId: Long) {
        dbRef.transactionWithContext(backgroundDispatcher) {
            dbRef.workoutSmartDatabaseQueries.deleteWorkout(workoutId)
        }
    }

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

    private fun getExercisesForWorkoutSync(workoutId: Long): List<ExerciseModel> =
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
                    image = exercise.image
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
                    id = history.id,
                    workoutName = history.workoutName,
                    date = history.date,
                    duration = history.duration
                )
            }
        }
        .flowOn(backgroundDispatcher)

    suspend fun insertHistory(workoutName: String, date: Long, duration: Long) {
        dbRef.transactionWithContext(backgroundDispatcher) {
            dbRef.workoutSmartDatabaseQueries.insertHistory(
                workoutName = workoutName,
                date = date,
                duration = duration
            )
        }
    }
}

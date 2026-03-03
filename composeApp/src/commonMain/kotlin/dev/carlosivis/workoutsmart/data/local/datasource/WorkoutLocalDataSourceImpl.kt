package dev.carlosivis.workoutsmart.data.local.datasource

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import app.cash.sqldelight.db.SqlDriver
import dev.carlosivis.workoutsmart.core.transactionWithContext
import dev.carlosivis.workoutsmart.database.WorkoutSmartDatabase
import dev.carlosivis.workoutsmart.models.ExerciseModel
import dev.carlosivis.workoutsmart.models.WorkoutModel
import dev.carlosivis.workoutsmart.models.WorkoutSummaryModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull

class WorkoutLocalDataSourceImpl(
    sqlDriver: SqlDriver,
    private val backgroundDispatcher: CoroutineDispatcher
): WorkoutLocalDataSource {
    private val dbRef: WorkoutSmartDatabase = WorkoutSmartDatabase.Companion(sqlDriver)


    override suspend fun insertWorkout(workout: WorkoutModel) {
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

    override suspend fun updateWorkout(workout: WorkoutModel) {
        dbRef.transactionWithContext(backgroundDispatcher) {
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

    override suspend fun deleteWorkout(workoutId: Long) {
        dbRef.transactionWithContext(backgroundDispatcher) {
            dbRef.workoutSmartDatabaseQueries.deleteWorkout(workoutId)
        }
    }

    override fun getAllWorkouts(): Flow<List<WorkoutSummaryModel>>
        = dbRef.workoutSmartDatabaseQueries
            .selectAllWorkouts()
            .asFlow()
            .mapToList(backgroundDispatcher)
            .map { workouts ->
                workouts.map { workout ->
                    WorkoutSummaryModel(
                        id = workout.id,
                        name = workout.name,
                        description = workout.description,
                    )
                }
            }
            .flowOn(backgroundDispatcher)

    override fun getWorkoutById(id: Long): Flow<WorkoutModel> =
        dbRef.workoutSmartDatabaseQueries
            .selectWorkoutById(id)
            .asFlow()
            .mapToOneOrNull(backgroundDispatcher)
            .mapNotNull { workout ->
                workout?.let {
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
}
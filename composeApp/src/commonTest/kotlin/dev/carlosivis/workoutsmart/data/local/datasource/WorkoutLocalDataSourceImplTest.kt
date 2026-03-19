package dev.carlosivis.workoutsmart.data.local.datasource

import dev.carlosivis.workoutsmart.models.ExerciseModel
import dev.carlosivis.workoutsmart.models.WorkoutModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class WorkoutLocalDataSourceImplTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var dataSource: WorkoutLocalDataSourceImpl

    private val exercise1 = ExerciseModel(
        id = 0, name = "Push Ups", notes = "Standard form",
        series = 3, repetitions = 15, image = null
    )

    private val exercise2 = ExerciseModel(
        id = 0, name = "Squats", notes = "Keep back straight",
        series = 4, repetitions = 12, image = null
    )

    private val workoutWithExercises = WorkoutModel(
        id = 0, name = "Full Body", description = "Complete workout",
        exercises = listOf(exercise1, exercise2)
    )

    private val workoutNoExercises = WorkoutModel(
        id = 0, name = "Cardio", description = "Light cardio session",
        exercises = emptyList()
    )

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        dataSource = WorkoutLocalDataSourceImpl(createTestDriver(), testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when no workouts then getAllWorkouts should return empty list`() = runTest {
        val result = dataSource.getAllWorkouts().first()

        assertTrue(result.isEmpty())
    }

    @Test
    fun `when insertWorkout then getAllWorkouts should return inserted workout`() = runTest {
        dataSource.insertWorkout(workoutWithExercises)
        testDispatcher.scheduler.advanceUntilIdle()

        val result = dataSource.getAllWorkouts().first()

        assertEquals(1, result.size)
        assertEquals("Full Body", result[0].name)
        assertEquals("Complete workout", result[0].description)
    }

    @Test
    fun `when insertWorkout multiple then getAllWorkouts should return all`() = runTest {
        dataSource.insertWorkout(workoutWithExercises)
        dataSource.insertWorkout(workoutNoExercises)
        testDispatcher.scheduler.advanceUntilIdle()

        val result = dataSource.getAllWorkouts().first()

        assertEquals(2, result.size)
    }

    @Test
    fun `when insertWorkout then getWorkoutById should return correct workout`() = runTest {
        dataSource.insertWorkout(workoutWithExercises)
        testDispatcher.scheduler.advanceUntilIdle()

        val insertedId = dataSource.getAllWorkouts().first().first().id
        val result = dataSource.getWorkoutById(insertedId).first()

        assertEquals("Full Body", result.name)
        assertEquals("Complete workout", result.description)
    }

    @Test
    fun `when insertWorkout with exercises then getWorkoutById should return exercises`() = runTest {
        dataSource.insertWorkout(workoutWithExercises)
        testDispatcher.scheduler.advanceUntilIdle()

        val insertedId = dataSource.getAllWorkouts().first().first().id
        val result = dataSource.getWorkoutById(insertedId).first()

        assertEquals(2, result.exercises.size)
        assertEquals("Push Ups", result.exercises[0].name)
        assertEquals(3, result.exercises[0].series)
        assertEquals(15, result.exercises[0].repetitions)
        assertEquals("Squats", result.exercises[1].name)
    }

    @Test
    fun `when insertWorkout with no exercises then getWorkoutById should return empty exercises`() = runTest {
        dataSource.insertWorkout(workoutNoExercises)
        testDispatcher.scheduler.advanceUntilIdle()

        val insertedId = dataSource.getAllWorkouts().first().first().id
        val result = dataSource.getWorkoutById(insertedId).first()

        assertTrue(result.exercises.isEmpty())
    }

    @Test
    fun `when updateWorkout then should update name and description`() = runTest {
        dataSource.insertWorkout(workoutWithExercises)
        testDispatcher.scheduler.advanceUntilIdle()

        val insertedId = dataSource.getAllWorkouts().first().first().id
        val updated = workoutWithExercises.copy(id = insertedId, name = "Updated", description = "New desc")

        dataSource.updateWorkout(updated)
        testDispatcher.scheduler.advanceUntilIdle()

        val result = dataSource.getWorkoutById(insertedId).first()

        assertEquals("Updated", result.name)
        assertEquals("New desc", result.description)
    }

    @Test
    fun `when updateWorkout then should replace exercises`() = runTest {
        dataSource.insertWorkout(workoutWithExercises)
        testDispatcher.scheduler.advanceUntilIdle()

        val insertedId = dataSource.getAllWorkouts().first().first().id
        val newExercise = ExerciseModel(0, "Pull Ups", "Wide grip", 3, 10, null)
        val updated = workoutWithExercises.copy(id = insertedId, exercises = listOf(newExercise))

        dataSource.updateWorkout(updated)
        testDispatcher.scheduler.advanceUntilIdle()

        val result = dataSource.getWorkoutById(insertedId).first()

        assertEquals(1, result.exercises.size)
        assertEquals("Pull Ups", result.exercises[0].name)
    }

    @Test
    fun `when updateWorkout with empty exercises then should remove all exercises`() = runTest {
        dataSource.insertWorkout(workoutWithExercises)
        testDispatcher.scheduler.advanceUntilIdle()

        val insertedId = dataSource.getAllWorkouts().first().first().id
        val updated = workoutWithExercises.copy(id = insertedId, exercises = emptyList())

        dataSource.updateWorkout(updated)
        testDispatcher.scheduler.advanceUntilIdle()

        val result = dataSource.getWorkoutById(insertedId).first()

        assertTrue(result.exercises.isEmpty())
    }

    @Test
    fun `when deleteWorkout then should remove from list`() = runTest {
        dataSource.insertWorkout(workoutWithExercises)
        testDispatcher.scheduler.advanceUntilIdle()

        val insertedId = dataSource.getAllWorkouts().first().first().id

        dataSource.deleteWorkout(insertedId)
        testDispatcher.scheduler.advanceUntilIdle()

        val result = dataSource.getAllWorkouts().first()

        assertTrue(result.isEmpty())
    }

    @Test
    fun `when deleteWorkout then should only remove target workout`() = runTest {
        dataSource.insertWorkout(workoutWithExercises)
        dataSource.insertWorkout(workoutNoExercises)
        testDispatcher.scheduler.advanceUntilIdle()

        val firstId = dataSource.getAllWorkouts().first().first().id

        dataSource.deleteWorkout(firstId)
        testDispatcher.scheduler.advanceUntilIdle()

        val result = dataSource.getAllWorkouts().first()

        assertEquals(1, result.size)
        assertNotNull(result.find { it.name == "Cardio" })
    }


    @Test
    fun `when insertWorkout with empty description then should store fallback`() = runTest {
        val workout = workoutWithExercises.copy(description = "")
        dataSource.insertWorkout(workout)
        testDispatcher.scheduler.advanceUntilIdle()

        val result = dataSource.getAllWorkouts().first().first()

        assertEquals("Sem descrição", result.description)
    }
}
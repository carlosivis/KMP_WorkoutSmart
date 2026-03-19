package dev.carlosivis.workoutsmart.data.repository

import app.cash.turbine.test
import dev.carlosivis.workoutsmart.data.local.datasource.HistoryLocalDataSource
import dev.carlosivis.workoutsmart.data.local.datasource.WorkoutLocalDataSource
import dev.carlosivis.workoutsmart.models.HistoryModel
import dev.carlosivis.workoutsmart.models.WorkoutModel
import dev.carlosivis.workoutsmart.models.WorkoutSummaryModel
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Clock

@OptIn(ExperimentalCoroutinesApi::class)
class WorkoutRepositoryImplTest {

    private val workoutLocalDataSource = mock<WorkoutLocalDataSource>()
    private val historyLocalDataSource = mock<HistoryLocalDataSource>()
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: WorkoutRepositoryImpl

    private val testWorkoutSummaries = listOf(
        WorkoutSummaryModel(
            id = 1,
            name = "Chest",
            description = "Chest workout description"),
        WorkoutSummaryModel(
            id = 2,
            name = "Back",
            description = "Back workout description"),
    )

    private val testWorkout = WorkoutModel(
        id = 1,
        name = "Chest Workout",
        description = "Description",
        exercises = emptyList()
    )

    private val testHistories = listOf(
        HistoryModel(
            id = 1,
            date = Clock.System.now().epochSeconds,
            workoutName = "Chest",
            duration = 3600L),
        HistoryModel(
                id = 2,
            date = Clock.System.now().epochSeconds - 40000L,
            workoutName = "Back",
            duration = 2400L),
    )

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = WorkoutRepositoryImpl(workoutLocalDataSource, historyLocalDataSource)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when getAllWorkouts then should return flow with workouts list`() = runTest {
        val flow = flowOf(testWorkoutSummaries)
        @Suppress("UNCHECKED_CAST")
        every { workoutLocalDataSource.getAllWorkouts() } returns flow

        repository.getAllWorkouts().test {
            val workouts = awaitItem()
            assertEquals(2, workouts.size)
            assertEquals("Chest", workouts[0].name)
            assertEquals("Back", workouts[1].name)
            awaitComplete()
        }
    }

    @Test
    fun `when getAllWorkouts returns empty list then should emit empty list`() = runTest {
        val flow = flowOf(emptyList<WorkoutSummaryModel>())
        @Suppress("UNCHECKED_CAST")
        every { workoutLocalDataSource.getAllWorkouts() } returns flow

        repository.getAllWorkouts().test {
            val workouts = awaitItem()
            assertEquals(0, workouts.size)
            awaitComplete()
        }
    }

    @Test
    fun `when getAllHistory then should return flow with history list`() = runTest {
        val flow = flowOf(testHistories)
        @Suppress("UNCHECKED_CAST")
        every { historyLocalDataSource.getAllHistory() } returns flow

        repository.getAllHistory().test {
            val histories = awaitItem()
            assertEquals(2, histories.size)
            assertEquals("Chest", histories[0].workoutName)
            assertEquals("Back", histories[1].workoutName)
            awaitComplete()
        }
    }

    @Test
    fun `when getAllHistory returns empty list then should emit empty list`() = runTest {
        val flow = flowOf(emptyList<HistoryModel>())
        @Suppress("UNCHECKED_CAST")
        every { historyLocalDataSource.getAllHistory() } returns flow

        repository.getAllHistory().test {
            val histories = awaitItem()
            assertEquals(0, histories.size)
            awaitComplete()
        }
    }

    @Test
    fun `when getWorkoutById with valid id then should return flow with workout`() = runTest {
        val workoutId = 1L
        val flow = flowOf(testWorkout)
        @Suppress("UNCHECKED_CAST")
        every { workoutLocalDataSource.getWorkoutById(workoutId) } returns flow

        repository.getWorkoutById(workoutId).test {
            val workout = awaitItem()
            assertEquals(1, workout.id)
            assertEquals("Chest Workout", workout.name)
            awaitComplete()
        }
    }

    @Test
    fun `when insertWorkout with valid workout then should call local data source`() = runTest {
        everySuspend { workoutLocalDataSource.insertWorkout(testWorkout) } returns Unit

        repository.insertWorkout(testWorkout)
        testDispatcher.scheduler.advanceUntilIdle()

        verifySuspend { workoutLocalDataSource.insertWorkout(testWorkout) }
    }

    @Test
    fun `when updateWorkout with valid workout then should call local data source`() = runTest {
        val updatedWorkout = testWorkout.copy(name = "Updated Chest")
        everySuspend { workoutLocalDataSource.updateWorkout(updatedWorkout) } returns Unit

        repository.updateWorkout(updatedWorkout)
        testDispatcher.scheduler.advanceUntilIdle()

        verifySuspend { workoutLocalDataSource.updateWorkout(updatedWorkout) }
    }

    @Test
    fun `when deleteWorkout with valid id then should call local data source`() = runTest {
        val workoutId = 1L
        everySuspend { workoutLocalDataSource.deleteWorkout(workoutId) } returns Unit

        repository.deleteWorkout(workoutId)
        testDispatcher.scheduler.advanceUntilIdle()

        verifySuspend { workoutLocalDataSource.deleteWorkout(workoutId) }
    }

    @Test
    fun `when insertHistory with workout details then should call history local data source`() = runTest {
        val workoutName = "Chest Workout"
        val timestamp = Clock.System.now().epochSeconds
        val duration = 3600L

        everySuspend { historyLocalDataSource.insertHistory(workoutName, timestamp, duration) } returns Unit

        repository.insertHistory(workoutName, timestamp, duration)
        testDispatcher.scheduler.advanceUntilIdle()

        verifySuspend { historyLocalDataSource.insertHistory(workoutName, timestamp, duration) }
    }

    @Test
    fun `when insertWorkout multiple times then should call data source each time`() = runTest {
        val workout1 = testWorkout
        val workout2 = testWorkout.copy(id = 2, name = "Back Workout")

        everySuspend { workoutLocalDataSource.insertWorkout(any()) } returns Unit

        repository.insertWorkout(workout1)
        repository.insertWorkout(workout2)
        testDispatcher.scheduler.advanceUntilIdle()

        verifySuspend { workoutLocalDataSource.insertWorkout(workout1) }
        verifySuspend { workoutLocalDataSource.insertWorkout(workout2) }
    }

    @Test
    fun `when deleteWorkout multiple times then should call data source each time`() = runTest {
        everySuspend { workoutLocalDataSource.deleteWorkout(any()) } returns Unit

        repository.deleteWorkout(1L)
        repository.deleteWorkout(2L)
        testDispatcher.scheduler.advanceUntilIdle()

        verifySuspend { workoutLocalDataSource.deleteWorkout(1L) }
        verifySuspend { workoutLocalDataSource.deleteWorkout(2L) }
    }

    @Test
    fun `when insertHistory with different durations then should save correct values`() = runTest {
        everySuspend { historyLocalDataSource.insertHistory(any(), any(), any()) } returns Unit

        val timestamp = Clock.System.now().epochSeconds
        repository.insertHistory("Cardio", timestamp, 1800L)
        repository.insertHistory("Weight", timestamp, 3600L)
        testDispatcher.scheduler.advanceUntilIdle()

        verifySuspend { historyLocalDataSource.insertHistory("Cardio", timestamp, 1800L) }
        verifySuspend { historyLocalDataSource.insertHistory("Weight", timestamp, 3600L) }
    }

    @Test
    fun `when getAllWorkouts multiple times then should emit same workouts`() = runTest {
        val flow = flowOf(testWorkoutSummaries)
        @Suppress("UNCHECKED_CAST")
        every { workoutLocalDataSource.getAllWorkouts() } returns flow

        repository.getAllWorkouts().test {
            val firstCall = awaitItem()
            assertEquals(2, firstCall.size)
            awaitComplete()
        }

        repository.getAllWorkouts().test {
            val secondCall = awaitItem()
            assertEquals(2, secondCall.size)
            awaitComplete()
        }
    }
}


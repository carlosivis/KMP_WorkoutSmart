package dev.carlosivis.workoutsmart.domain.usecase

import dev.carlosivis.features.workoutlog.WorkoutLogRequest
import dev.carlosivis.features.workoutlog.WorkoutType
import dev.carlosivis.workoutsmart.domain.repository.SocialRepository
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class RegisterWorkoutLogUseCaseTest {

    private val repository = mock<SocialRepository>()
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var useCase: RegisterWorkoutLogUseCase

    private val testWorkoutLogRequest = WorkoutLogRequest(
        type = WorkoutType.GYM,
        description = "Chest and triceps workout",
        durationInSeconds = 3600L
    )

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        useCase = RegisterWorkoutLogUseCase(repository, testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when execute with valid workout log then should return success`() = runTest {
        everySuspend { repository.registerWorkoutLog(testWorkoutLogRequest) } returns Result.success(Unit)

        val result = useCase(testWorkoutLogRequest)

        assertTrue(result.isSuccess)
        assertEquals(Unit, result.getOrNull())
        verifySuspend { repository.registerWorkoutLog(testWorkoutLogRequest) }
    }

    @Test
    fun `when execute with different workout types then should pass correct parameters to repository`() = runTest {
        val gymWorkout = WorkoutLogRequest(WorkoutType.GYM, "Gym session", 3600L)
        val runningWorkout = WorkoutLogRequest(WorkoutType.RUNNING, "Morning run", 1800L)
        val cyclingWorkout = WorkoutLogRequest(WorkoutType.CYCLING, "Bike ride", 2400L)

        everySuspend { repository.registerWorkoutLog(gymWorkout) } returns Result.success(Unit)
        everySuspend { repository.registerWorkoutLog(runningWorkout) } returns Result.success(Unit)
        everySuspend { repository.registerWorkoutLog(cyclingWorkout) } returns Result.success(Unit)

        val result1 = useCase(gymWorkout)
        val result2 = useCase(runningWorkout)
        val result3 = useCase(cyclingWorkout)

        assertTrue(result1.isSuccess)
        assertTrue(result2.isSuccess)
        assertTrue(result3.isSuccess)
        verifySuspend { repository.registerWorkoutLog(gymWorkout) }
        verifySuspend { repository.registerWorkoutLog(runningWorkout) }
        verifySuspend { repository.registerWorkoutLog(cyclingWorkout) }
    }

    @Test
    fun `when execute and repository throws exception then should return failure`() = runTest {
        val error = Exception("Failed to register workout")
        everySuspend { repository.registerWorkoutLog(testWorkoutLogRequest) } returns Result.failure(error)

        val result = useCase(testWorkoutLogRequest)

        assertTrue(result.isFailure)
        assertEquals(error.message, result.exceptionOrNull()?.message)
        verifySuspend { repository.registerWorkoutLog(testWorkoutLogRequest) }
    }

    @Test
    fun `when execute with null optional fields then should still register successfully`() = runTest {
        val minimalWorkout = WorkoutLogRequest(
            type = WorkoutType.OTHER,
            description = null,
            durationInSeconds = null
        )
        everySuspend { repository.registerWorkoutLog(minimalWorkout) } returns Result.success(Unit)

        val result = useCase(minimalWorkout)

        assertTrue(result.isSuccess)
        verifySuspend { repository.registerWorkoutLog(minimalWorkout) }
    }

    @Test
    fun `when execute with network error then should return failure with proper message`() = runTest {
        val networkError = Exception("Network timeout")
        everySuspend { repository.registerWorkoutLog(testWorkoutLogRequest) } returns Result.failure(networkError)

        val result = useCase(testWorkoutLogRequest)

        assertTrue(result.isFailure)
        assertEquals("Network timeout", result.exceptionOrNull()?.message)
    }

    @Test
    fun `when execute multiple times then should call repository each time`() = runTest {
        everySuspend { repository.registerWorkoutLog(testWorkoutLogRequest) } returns Result.success(Unit)

        val result1 = useCase(testWorkoutLogRequest)
        val result2 = useCase(testWorkoutLogRequest)

        assertTrue(result1.isSuccess)
        assertTrue(result2.isSuccess)
    }

    @Test
    fun `when execute with long duration workout then should register correctly`() = runTest {
        val longWorkout = WorkoutLogRequest(
            type = WorkoutType.CYCLING,
            description = "Long distance cycling",
            durationInSeconds = 18000L // 5 hours
        )
        everySuspend { repository.registerWorkoutLog(longWorkout) } returns Result.success(Unit)

        val result = useCase(longWorkout)

        assertTrue(result.isSuccess)
        verifySuspend { repository.registerWorkoutLog(longWorkout) }
    }
}


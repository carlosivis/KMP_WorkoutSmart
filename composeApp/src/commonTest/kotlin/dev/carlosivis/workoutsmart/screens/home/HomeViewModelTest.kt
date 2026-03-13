package dev.carlosivis.workoutsmart.screens.home

import app.cash.turbine.test
import dev.carlosivis.features.workoutlog.WorkoutLogRequest
import dev.carlosivis.features.workoutlog.WorkoutType
import dev.carlosivis.workoutsmart.domain.repository.AuthRepository
import dev.carlosivis.workoutsmart.domain.repository.SocialRepository
import dev.carlosivis.workoutsmart.domain.repository.WorkoutRepository
import dev.carlosivis.workoutsmart.domain.usecase.GetGroupsUseCase
import dev.carlosivis.workoutsmart.domain.usecase.GetUserUseCase
import dev.carlosivis.workoutsmart.domain.usecase.RegisterWorkoutLogUseCase
import dev.carlosivis.workoutsmart.models.GroupResponse
import dev.carlosivis.workoutsmart.navigation.navigator.HomeNavigator
import dev.mokkery.MockMode
import dev.mokkery.answering.calls
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val workoutRepository = mock<WorkoutRepository>()
    private val authRepository = mock<AuthRepository>()
    private val socialRepository = mock<SocialRepository>()
    private val getUserUseCase = GetUserUseCase(authRepository, testDispatcher)
    private val getGroupsUseCase = GetGroupsUseCase(socialRepository, testDispatcher)
    private val registerWorkoutLogUseCase = RegisterWorkoutLogUseCase(socialRepository, testDispatcher)
    private val navigator = mock<HomeNavigator>(MockMode.autofill)

    private val testGroups = listOf(GroupResponse(1, "Group 1", "CODE1", 100L, 1))

    private fun buildViewModel() = HomeViewModel(
        workoutRepository,
        getUserUseCase,
        getGroupsUseCase,
        registerWorkoutLogUseCase,
        navigator
    )

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { workoutRepository.getAllWorkouts() } returns flowOf(emptyList())
        every { workoutRepository.getAllHistory() } returns flowOf(emptyList())
        runBlocking {
            everySuspend { authRepository.getUser() } returns Result.success(null)
            everySuspend { socialRepository.getGroups() } returns Result.success(emptyList())
        }
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when init then should fetch user and groups`() = runTest {
        buildViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        verify(VerifyMode.exactly(1)) { workoutRepository.getAllWorkouts() }
        verify(VerifyMode.exactly(1)) { workoutRepository.getAllHistory() }
        verifySuspend(VerifyMode.exactly(1)) { authRepository.getUser() }
        verifySuspend(VerifyMode.exactly(1)) { socialRepository.getGroups() }
    }

    @Test
    fun `when dispatch AttemptDeleteWorkout then should update state`() = runTest {
        val viewModel = buildViewModel()

        viewModel.state.test {
            awaitItem()
            viewModel.dispatchAction(HomeViewAction.AttemptDeleteWorkout(1L, "Chest Day"))
            val state = awaitItem()
            assertEquals(1L, state.workoutIdToDelete)
            assertEquals("Chest Day", state.workoutToDelete)
        }
    }

    @Test
    fun `when dispatch ConfirmDeleteWorkout then should call repository`() = runTest {
        everySuspend { workoutRepository.deleteWorkout(any()) } returns Unit
        val viewModel = buildViewModel()

        viewModel.dispatchAction(HomeViewAction.AttemptDeleteWorkout(1L, "Test"))
        viewModel.dispatchAction(HomeViewAction.ConfirmDeleteWorkout)
        testDispatcher.scheduler.advanceUntilIdle()

        verifySuspend(VerifyMode.exactly(1)) { workoutRepository.deleteWorkout(1L) }
    }

    @Test
    fun `when dispatch ConfirmDeleteWorkout fails then should set error state`() = runTest {
        val error = Exception("Delete failed")
        everySuspend { workoutRepository.deleteWorkout(any()) } calls { throw error }
        val viewModel = buildViewModel()

        viewModel.dispatchAction(HomeViewAction.AttemptDeleteWorkout(1L, "Test"))
        viewModel.dispatchAction(HomeViewAction.ConfirmDeleteWorkout)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            awaitItem()
            assertEquals("Delete failed", awaitItem().error)
        }
    }

    @Test
    fun `when dispatch RegisterWorkoutLog then should show message`() = runTest {
        val workoutLog = WorkoutLogRequest(WorkoutType.GYM, "test", 6000)
        everySuspend { socialRepository.registerWorkoutLog(workoutLog) } returns Result.success(Unit)

        val viewModel = buildViewModel()
        viewModel.dispatchAction(HomeViewAction.RegisterWorkoutLog(workoutLog))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            awaitItem()
            assertNotNull(awaitItem().message)
        }
        verifySuspend(VerifyMode.exactly(1)) { socialRepository.registerWorkoutLog(workoutLog) }
    }

    @Test
    fun `when dispatch RegisterWorkoutLog fails then should set error state`() = runTest {
        val error = Exception("Registration failed")
        val workoutLog = WorkoutLogRequest(WorkoutType.GYM, "test", 6000)
        everySuspend { socialRepository.registerWorkoutLog(workoutLog) } returns Result.failure(error)

        val viewModel = buildViewModel()
        viewModel.dispatchAction(HomeViewAction.RegisterWorkoutLog(workoutLog))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            awaitItem()
            assertEquals("Registration failed", awaitItem().error)
        }
    }

    @Test
    fun `when dispatch ShowRegisterWorkoutDialog then should toggle visibility`() = runTest {
        val viewModel = buildViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            assertFalse(awaitItem().showRegisterWorkoutDialog)

            viewModel.dispatchAction(HomeViewAction.ShowRegisterWorkoutDialog)
            assertTrue(awaitItem().showRegisterWorkoutDialog)

            viewModel.dispatchAction(HomeViewAction.ShowRegisterWorkoutDialog)
            assertFalse(awaitItem().showRegisterWorkoutDialog)
        }
    }

    @Test
    fun `when dispatch Refresh then should fetch user and groups again`() = runTest {
        val viewModel = buildViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.dispatchAction(HomeViewAction.Refresh)
        testDispatcher.scheduler.advanceUntilIdle()

        verifySuspend(VerifyMode.exactly(2)) { authRepository.getUser() }
        verifySuspend(VerifyMode.exactly(2)) { socialRepository.getGroups() }
    }

    @Test
    fun `when dispatch CleanMessages then should clear error and message`() = runTest {
        val error = Exception("Registration failed")
        val workoutLog = WorkoutLogRequest(WorkoutType.GYM, "test", 6000)
        everySuspend { socialRepository.registerWorkoutLog(workoutLog) } returns Result.failure(error)

        val viewModel = buildViewModel()
        viewModel.dispatchAction(HomeViewAction.RegisterWorkoutLog(workoutLog))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            awaitItem()
            assertEquals("Registration failed", awaitItem().error)
        }

        viewModel.dispatchAction(HomeViewAction.CleanMessages)

        viewModel.state.test {
            awaitItem()
            val state = awaitItem()
            assertNull(state.error)
            assertNull(state.message)
        }
    }

    @Test
    fun `when dispatch Navigate CreateWorkout then should call navigator`() = runTest {
        buildViewModel().dispatchAction(HomeViewAction.Navigate.CreateWorkout)
        verify { navigator.toCreateWorkout() }
    }

    @Test
    fun `when dispatch Navigate Workout then should call navigator with id`() = runTest {
        buildViewModel().dispatchAction(HomeViewAction.Navigate.Workout(1L))
        verify { navigator.toActiveWorkout(1L) }
    }

    @Test
    fun `when dispatch Navigate Edit then should call navigator with id`() = runTest {
        buildViewModel().dispatchAction(HomeViewAction.Navigate.Edit(1L))
        verify { navigator.toEditWorkout(1L) }
    }

    @Test
    fun `when dispatch Navigate Profile then should call navigator`() = runTest {
        buildViewModel().dispatchAction(HomeViewAction.Navigate.Profile)
        verify { navigator.toProfile() }
    }

    @Test
    fun `when dispatch Navigate Groups then should call navigator with groups`() = runTest {
        everySuspend { socialRepository.getGroups() } returns Result.success(testGroups)

        val viewModel = buildViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.dispatchAction(HomeViewAction.Navigate.Groups)
        verify { navigator.toGroups(testGroups) }
    }

    @Test
    fun `when dispatch Navigate Ranking then should call navigator with group`() = runTest {
        val group = testGroups.first()
        buildViewModel().dispatchAction(HomeViewAction.Navigate.Ranking(group))
        verify { navigator.toRanking(group) }
    }
}
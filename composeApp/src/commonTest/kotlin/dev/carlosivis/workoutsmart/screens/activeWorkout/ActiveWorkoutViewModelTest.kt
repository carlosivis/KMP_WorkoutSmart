package dev.carlosivis.workoutsmart.screens.activeWorkout

import dev.carlosivis.workoutsmart.domain.repository.SettingsRepository
import dev.carlosivis.workoutsmart.domain.repository.SocialRepository
import dev.carlosivis.workoutsmart.domain.repository.WorkoutRepository
import dev.carlosivis.workoutsmart.domain.usecase.RegisterWorkoutLogUseCase
import dev.carlosivis.workoutsmart.models.ExerciseModel
import dev.carlosivis.workoutsmart.models.SettingsModel
import dev.carlosivis.workoutsmart.models.WorkoutModel
import dev.carlosivis.workoutsmart.screens.components.expect.VibratorHelper
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
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ActiveWorkoutViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val workoutId = 1L

    private val repository = mock<WorkoutRepository>()
    private val settingsRepository = mock<SettingsRepository>()
    private val socialRepository = mock<SocialRepository>()
    private val registerWorkoutLogUseCase = RegisterWorkoutLogUseCase(socialRepository, testDispatcher)
    private val vibratorHelper = mock<VibratorHelper>()

    private var navigateBackCalled = false
    private val onNavigateBack: () -> Unit = { navigateBackCalled = true }

    private val squat = ExerciseModel(1, "Squat", "Notes", 3, 10, null)

    private fun buildViewModel() = ActiveWorkoutViewModel(
        workoutId, repository, settingsRepository,
        registerWorkoutLogUseCase, onNavigateBack, vibratorHelper
    )

    private fun workoutWith(vararg exercises: ExerciseModel) =
        WorkoutModel(workoutId, "Leg Day", "Desc", exercises.toList())

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { settingsRepository.getSettings() } returns flowOf(SettingsModel.default())
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
        navigateBackCalled = false
    }

    @Test
    fun `when dispatch GetWorkout then should fetch workout and update state`() = runTest {
        val workout = workoutWith(squat)
        every { repository.getWorkoutById(workoutId) } returns flowOf(workout)

        val viewModel = buildViewModel()
        viewModel.dispatchAction(ActiveWorkoutViewAction.GetWorkout)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(workout, viewModel.state.value.workout)
        assertEquals(3, viewModel.state.value.remainingSeries["Squat"])
        verify(VerifyMode.exactly(1)) { repository.getWorkoutById(workoutId) }
    }

    @Test
    fun `when dispatch StopWorkout then should show exit dialog`() = runTest {
        val viewModel = buildViewModel()

        viewModel.dispatchAction(ActiveWorkoutViewAction.StopWorkout)

        assertTrue(viewModel.state.value.showExitUnfinishedDialog)
    }

    @Test
    fun `when dispatch SaveWorkoutHistory then should call repository, usecase and navigate back`() = runTest {
        everySuspend { repository.insertHistory(any(), any(), any()) } returns Unit
        everySuspend { socialRepository.registerWorkoutLog(any()) } returns Result.success(Unit)
        every { repository.getWorkoutById(workoutId) } returns flowOf(workoutWith())

        val viewModel = buildViewModel()
        viewModel.dispatchAction(ActiveWorkoutViewAction.GetWorkout)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.dispatchAction(ActiveWorkoutViewAction.SaveWorkoutHistory)
        testDispatcher.scheduler.advanceUntilIdle()

        verifySuspend(VerifyMode.exactly(1)) { repository.insertHistory(any(), any(), any()) }
        verifySuspend(VerifyMode.exactly(1)) { socialRepository.registerWorkoutLog(any()) }
        assertTrue(navigateBackCalled)
    }

    @Test
    fun `when dispatch ExitWithoutSave then should navigate back without saving`() = runTest {
        val viewModel = buildViewModel()

        viewModel.dispatchAction(ActiveWorkoutViewAction.ExitWithoutSave)

        assertTrue(navigateBackCalled)
        verifySuspend(VerifyMode.not) { repository.insertHistory(any(), any(), any()) }
    }

    @Test
    fun `when dispatch AttemptToNavigateBack then should show confirmation dialog`() = runTest {
        val viewModel = buildViewModel()

        viewModel.dispatchAction(ActiveWorkoutViewAction.AttemptToNavigateBack)

        assertTrue(viewModel.state.value.showExitConfirmationDialog)
    }

    @Test
    fun `when dispatch CancelNavigateBack then should hide confirmation dialog`() = runTest {
        val viewModel = buildViewModel()

        viewModel.dispatchAction(ActiveWorkoutViewAction.AttemptToNavigateBack)
        assertTrue(viewModel.state.value.showExitConfirmationDialog)

        viewModel.dispatchAction(ActiveWorkoutViewAction.CancelNavigateBack)
        assertFalse(viewModel.state.value.showExitConfirmationDialog)
    }

    @Test
    fun `when dispatch UpdateRestTime then should update rest time and hide selector`() = runTest {
        val viewModel = buildViewModel()

        viewModel.dispatchAction(ActiveWorkoutViewAction.ToggleRestTimer)
        assertTrue(viewModel.state.value.showRestTimerSelector)

        viewModel.dispatchAction(ActiveWorkoutViewAction.UpdateRestTime(60))

        assertEquals(60, viewModel.state.value.restTime)
        assertFalse(viewModel.state.value.showRestTimerSelector)
    }

    @Test
    fun `when dispatch ToggleRestTimer then should toggle selector visibility`() = runTest {
        val viewModel = buildViewModel()

        assertFalse(viewModel.state.value.showRestTimerSelector)

        viewModel.dispatchAction(ActiveWorkoutViewAction.ToggleRestTimer)
        assertTrue(viewModel.state.value.showRestTimerSelector)

        viewModel.dispatchAction(ActiveWorkoutViewAction.ToggleRestTimer)
        assertFalse(viewModel.state.value.showRestTimerSelector)
    }

    @Test
    fun `when dispatch CleanMessages then should clear error and message`() = runTest {
        val viewModel = buildViewModel()

        viewModel.dispatchAction(ActiveWorkoutViewAction.CleanMessages)

        assertNull(viewModel.state.value.error)
        assertNull(viewModel.state.value.message)
    }
}
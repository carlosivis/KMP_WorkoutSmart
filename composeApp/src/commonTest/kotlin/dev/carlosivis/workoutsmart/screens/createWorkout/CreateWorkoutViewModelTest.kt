package dev.carlosivis.workoutsmart.screens.createWorkout

import dev.carlosivis.workoutsmart.domain.repository.WorkoutRepository
import dev.carlosivis.workoutsmart.models.ExerciseModel
import dev.carlosivis.workoutsmart.models.WorkoutModel
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify
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
class CreateWorkoutViewModelTest {

    private val repository = mock<WorkoutRepository>()
    private var navigateBackCalled = false
    private val onNavigateBack: () -> Unit = { navigateBackCalled = true }
    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
        navigateBackCalled = false
    }

    private fun buildViewModel(workoutId: Long? = null) =
        CreateWorkoutViewModel(repository, onNavigateBack, workoutId)

    @Test
    fun `when dispatch AddName then should update state`() = runTest {
        val viewModel = buildViewModel()

        viewModel.dispatchAction(CreateWorkoutViewAction.AddName("New Workout"))
        testDispatcher.scheduler.runCurrent()

        assertEquals("New Workout", viewModel.state.value.workout.name)
    }

    @Test
    fun `when dispatch AddDescription then should update state`() = runTest {
        val viewModel = buildViewModel()

        viewModel.dispatchAction(CreateWorkoutViewAction.AddDescription("New Description"))
        testDispatcher.scheduler.runCurrent()

        assertEquals("New Description", viewModel.state.value.workout.description)
    }

    @Test
    fun `when dispatch SaveWorkout then should call repository and navigate back`() = runTest {
        everySuspend { repository.insertWorkout(any()) } returns Unit
        val viewModel = buildViewModel()

        viewModel.dispatchAction(CreateWorkoutViewAction.AddName("Test"))
        viewModel.dispatchAction(CreateWorkoutViewAction.SaveWorkout)
        testDispatcher.scheduler.advanceUntilIdle()

        verifySuspend { repository.insertWorkout(any()) }
        assertTrue(navigateBackCalled)
    }

    @Test
    fun `when dispatch InitializeEditMode then should fetch workout and enable edit mode`() = runTest {
        val workoutId = 1L
        val workout = WorkoutModel(workoutId, "Edit Me", "Desc", emptyList())
        every { repository.getWorkoutById(workoutId) } returns flowOf(workout)

        val viewModel = buildViewModel(workoutId)

        viewModel.dispatchAction(CreateWorkoutViewAction.InitializeEditMode)
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.state.value.isEditMode)
        assertEquals(workout, viewModel.state.value.workout)
        verify { repository.getWorkoutById(workoutId) }
    }

    @Test
    fun `when adding exercise and confirm then should update exercise list and close dialog`() = runTest {
        val viewModel = buildViewModel()
        val exercise = ExerciseModel(1, "Push up", "Notes", 3, 10, null)

        viewModel.dispatchAction(CreateWorkoutViewAction.StartAddingExercise)
        testDispatcher.scheduler.runCurrent()
        assertTrue(viewModel.state.value.isAddingExercise)

        viewModel.dispatchAction(CreateWorkoutViewAction.UpdateNewExercise(exercise))
        viewModel.dispatchAction(CreateWorkoutViewAction.ConfirmNewExercise)
        testDispatcher.scheduler.runCurrent()

        assertEquals(1, viewModel.state.value.workout.exercises.size)
        assertEquals(exercise.name, viewModel.state.value.workout.exercises[0].name)
        assertFalse(viewModel.state.value.isAddingExercise)
    }

    @Test
    fun `when dispatch RemoveExercise then should remove from list`() = runTest {
        val viewModel = buildViewModel()
        val exercise = ExerciseModel(1, "Push up", "Notes", 3, 10, null)

        viewModel.dispatchAction(CreateWorkoutViewAction.UpdateNewExercise(exercise))
        viewModel.dispatchAction(CreateWorkoutViewAction.ConfirmNewExercise)
        testDispatcher.scheduler.runCurrent()
        assertEquals(1, viewModel.state.value.workout.exercises.size)

        viewModel.dispatchAction(CreateWorkoutViewAction.RemoveExercise(1))
        testDispatcher.scheduler.runCurrent()
        assertEquals(0, viewModel.state.value.workout.exercises.size)
    }

    @Test
    fun `when dispatch UpdateExercise then should update exercise in list`() = runTest {
        val viewModel = buildViewModel()
        val exercise1 = ExerciseModel(1, "Push up", "Notes", 3, 10, null)
        val exercise2 = ExerciseModel(2, "Pull up", "Updated", 4, 12, null)

        viewModel.dispatchAction(CreateWorkoutViewAction.UpdateNewExercise(exercise1))
        viewModel.dispatchAction(CreateWorkoutViewAction.ConfirmNewExercise)
        testDispatcher.scheduler.runCurrent()

        viewModel.dispatchAction(CreateWorkoutViewAction.UpdateExercise(0, exercise2))
        testDispatcher.scheduler.runCurrent()
        assertEquals("Pull up", viewModel.state.value.workout.exercises[0].name)
    }

    @Test
    fun `when dispatch CancelAddingExercise then should set isAddingExercise to false`() = runTest {
        val viewModel = buildViewModel()

        viewModel.dispatchAction(CreateWorkoutViewAction.StartAddingExercise)
        testDispatcher.scheduler.runCurrent()
        assertTrue(viewModel.state.value.isAddingExercise)

        viewModel.dispatchAction(CreateWorkoutViewAction.CancelAddingExercise)
        testDispatcher.scheduler.runCurrent()
        assertFalse(viewModel.state.value.isAddingExercise)
    }

    @Test
    fun `when dispatch AttemptToNavigateBack with unsaved changes then should show dialog`() = runTest {
        val viewModel = buildViewModel()

        viewModel.dispatchAction(CreateWorkoutViewAction.AddName("New"))
        testDispatcher.scheduler.runCurrent()

        viewModel.dispatchAction(CreateWorkoutViewAction.AttemptToNavigateBack)
        testDispatcher.scheduler.runCurrent()
        assertTrue(viewModel.state.value.showExitConfirmationDialog)
    }

    @Test
    fun `when dispatch CancelNavigateBack then should hide dialog`() = runTest {
        val viewModel = buildViewModel()

        viewModel.dispatchAction(CreateWorkoutViewAction.AddName("New"))
        viewModel.dispatchAction(CreateWorkoutViewAction.AttemptToNavigateBack)
        testDispatcher.scheduler.runCurrent()
        assertTrue(viewModel.state.value.showExitConfirmationDialog)

        viewModel.dispatchAction(CreateWorkoutViewAction.CancelNavigateBack)
        testDispatcher.scheduler.runCurrent()
        assertFalse(viewModel.state.value.showExitConfirmationDialog)
    }

    @Test
    fun `when dispatch ToggleCameraPermissionDialog then should toggle`() = runTest {
        val viewModel = buildViewModel()

        assertFalse(viewModel.state.value.showCameraPermissionDialog)

        viewModel.dispatchAction(CreateWorkoutViewAction.ToggleCameraPermissionDialog)
        testDispatcher.scheduler.runCurrent()
        assertTrue(viewModel.state.value.showCameraPermissionDialog)

        viewModel.dispatchAction(CreateWorkoutViewAction.ToggleCameraPermissionDialog)
        testDispatcher.scheduler.runCurrent()
        assertFalse(viewModel.state.value.showCameraPermissionDialog)
    }

    @Test
    fun `when dispatch RequestImageSource then should show dialog`() = runTest {
        val viewModel = buildViewModel()

        viewModel.dispatchAction(CreateWorkoutViewAction.RequestImageSource(0))
        testDispatcher.scheduler.runCurrent()
        assertTrue(viewModel.state.value.showImageSourceDialog)
    }

    @Test
    fun `when dispatch CleanMessages then should clear error and message`() = runTest {
        val viewModel = buildViewModel()

        viewModel.dispatchAction(CreateWorkoutViewAction.CleanMessages)
        testDispatcher.scheduler.runCurrent()

        assertNull(viewModel.state.value.error)
        assertNull(viewModel.state.value.message)
    }
}
package dev.carlosivis.workoutsmart.screens.createWorkout

import androidx.lifecycle.ViewModel
import dev.carlosivis.workoutsmart.models.ExerciseModel
import dev.carlosivis.workoutsmart.repository.WorkoutRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CreateWorkoutViewModel(
    private val repository: WorkoutRepository,
    private val onNavigateBack: () -> Unit
): ViewModel() {

    private val viewModelScope = CoroutineScope(Dispatchers.Main)

    private val _state = MutableStateFlow(CreateWorkoutViewState())
    val state = _state.asStateFlow()

    fun dispatchAction(action: CreateWorkoutViewAction) {
        when (action) {
            is CreateWorkoutViewAction.SaveWorkout -> saveWorkout()
            is CreateWorkoutViewAction.RemoveExercise -> removeExercise(action.exerciseId)
            is CreateWorkoutViewAction.GetExercises -> TODO()
            is CreateWorkoutViewAction.NavigateBack -> onNavigateBack()
            is CreateWorkoutViewAction.AddName -> addName(action.name)
            is CreateWorkoutViewAction.AddDescription -> addDescription(action.description)
            is CreateWorkoutViewAction.UpdateExercise -> updateExercise(action.index, action.exercise)
            is CreateWorkoutViewAction.StartAddingExercise -> startAddingExercise()
            is CreateWorkoutViewAction.CancelAddingExercise -> cancelAddingExercise()
            is CreateWorkoutViewAction.UpdateNewExercise -> updateNewExercise(action.exercise)
            is CreateWorkoutViewAction.ConfirmNewExercise -> confirmNewExercise()
        }
    }

    private fun saveWorkout() {
        viewModelScope.launch {
            setLoading(true)
            try {
                repository.insertWorkout(_state.value.workout)
                onNavigateBack()
            } finally {
                setLoading(false)
            }
        }
    }

    private fun setLoading(isLoading: Boolean) {
        _state.update { it.copy(isLoading = isLoading) }
    }

    private fun confirmNewExercise() {
        _state.update {
            val updatedExercises = it.workout.exercises.toMutableList()
            updatedExercises.add(it.newExercise)
            it.copy(
                workout = it.workout.copy(exercises = updatedExercises),
                isAddingExercise = false,
                newExercise = ExerciseModel.empty()
            )
        }
    }

    private fun removeExercise(exerciseId: Int) {
        _state.update {
            val updatedExercises = it.workout.exercises.filter { exercise ->
                exercise.id != exerciseId }
            it.copy(workout = it.workout.copy(exercises = updatedExercises))
        }
    }

    private fun addName(name: String) {
        _state.update {
            it.copy(workout = it.workout.copy(name = name))
        }
    }

    private fun addDescription(description: String) {
        _state.update {
            it.copy(workout = it.workout.copy(description = description))
        }
    }

    private fun updateExercise(index: Int, exercise: ExerciseModel) {
        _state.update {
            val mutableExercises = it.workout.exercises.toMutableList()
            if (mutableExercises?.indices?.contains(index) == true ) {
                mutableExercises.set(index, exercise)
            }
            it.copy(workout = it.workout.copy(exercises = mutableExercises))
        }
    }

    private fun startAddingExercise() {
        _state.update { it.copy(isAddingExercise = true, newExercise = ExerciseModel.empty()) }
    }

    private fun cancelAddingExercise() {
        _state.update { it.copy(isAddingExercise = false, newExercise = ExerciseModel.empty()) }
    }

    private fun updateNewExercise(exercise: ExerciseModel) {
        _state.update { it.copy(newExercise = exercise) }
    }
}


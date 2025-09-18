package dev.carlosivis.workoutsmart.screens.createWorkout

import dev.carlosivis.workoutsmart.models.ExerciseModel
import io.github.ismoy.imagepickerkmp.domain.models.PhotoResult // Add this import

sealed class CreateWorkoutViewAction {
    object GetExercises : CreateWorkoutViewAction()
    data class AddName(val name: String) : CreateWorkoutViewAction()
    data class AddDescription(val description: String) : CreateWorkoutViewAction()
    object StartAddingExercise : CreateWorkoutViewAction()
    object CancelAddingExercise : CreateWorkoutViewAction()
    data class UpdateNewExercise(val exercise: ExerciseModel) : CreateWorkoutViewAction()
    object ConfirmNewExercise : CreateWorkoutViewAction()
    data class UpdateExercise(val index: Int, val exercise: ExerciseModel) : CreateWorkoutViewAction()
    data class RemoveExercise(val exerciseId: Int) : CreateWorkoutViewAction()
    object SaveWorkout : CreateWorkoutViewAction()
    object NavigateBack : CreateWorkoutViewAction()
    object AttemptToNavigateBack : CreateWorkoutViewAction()
    object CancelNavigateBack : CreateWorkoutViewAction()

    // New actions for image handling
    data class UpdateNewExerciseImage(val photoResult: PhotoResult) : CreateWorkoutViewAction()
    data class UpdateExistingExerciseImage(val index: Int, val photoResult: PhotoResult) : CreateWorkoutViewAction()
}

package dev.carlosivis.workoutsmart.screens.createWorkout

import dev.carlosivis.workoutsmart.models.ExerciseModel

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
    data class UpdateNewExerciseImage(val image: ByteArray) : CreateWorkoutViewAction()
    data class UpdateExistingExerciseImage(val index: Int, val image: ByteArray) : CreateWorkoutViewAction()
}
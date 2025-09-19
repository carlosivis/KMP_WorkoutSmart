package dev.carlosivis.workoutsmart.screens.createWorkout

import dev.carlosivis.workoutsmart.models.ExerciseModel
import dev.carlosivis.workoutsmart.models.WorkoutModel
import dev.carlosivis.workoutsmart.platform.PermissionType

data class CreateWorkoutViewState(
    val isLoading: Boolean = false,
    val showExitConfirmationDialog: Boolean = false,
    val errorMessage: String? = null,
    val workout: WorkoutModel = WorkoutModel.empty(),
    val isAddingExercise: Boolean = false,
    val newExercise: ExerciseModel = ExerciseModel.empty(),
    val permissionToRequest: PermissionType? = null,
    val launchCamera: Boolean = false,
    val launchGallery: Boolean = false
)

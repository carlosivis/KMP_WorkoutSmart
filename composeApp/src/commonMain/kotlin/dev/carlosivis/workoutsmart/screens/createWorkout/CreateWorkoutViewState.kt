package dev.carlosivis.workoutsmart.screens.createWorkout

import dev.carlosivis.workoutsmart.models.ExerciseModel
import dev.carlosivis.workoutsmart.models.WorkoutModel

data class CreateWorkoutViewState(
    val isLoading: Boolean = false,
    val showExitConfirmationDialog: Boolean = false,
    val errorMessage: String? = null,
    val workout: WorkoutModel = WorkoutModel.empty(),
    val isAddingExercise: Boolean = false,
    val newExercise: ExerciseModel = ExerciseModel.empty(),
    val showCamera: Boolean = false,
    val showCameraPermissionDialog: Boolean = false,
    val cameraPermissionGranted: Boolean = false,
    val targetExerciseIndex: Int? = null,
    val showImageSourceDialog: Boolean = false,
    val capturedPhotoPreview: ByteArray? = null,
    val showPhotoPreviewDialog: Boolean = false,
    val isEditMode: Boolean = false,
    val originalWorkout: WorkoutModel? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as CreateWorkoutViewState

        if (isLoading != other.isLoading) return false
        if (showExitConfirmationDialog != other.showExitConfirmationDialog) return false
        if (isAddingExercise != other.isAddingExercise) return false
        if (showCamera != other.showCamera) return false
        if (showCameraPermissionDialog != other.showCameraPermissionDialog) return false
        if (cameraPermissionGranted != other.cameraPermissionGranted) return false
        if (targetExerciseIndex != other.targetExerciseIndex) return false
        if (showImageSourceDialog != other.showImageSourceDialog) return false
        if (showPhotoPreviewDialog != other.showPhotoPreviewDialog) return false
        if (errorMessage != other.errorMessage) return false
        if (workout != other.workout) return false
        if (newExercise != other.newExercise) return false
        if (!capturedPhotoPreview.contentEquals(other.capturedPhotoPreview)) return false
        if (isEditMode != other.isEditMode) return false
        if (originalWorkout != other.originalWorkout) return false

        return true
    }

    override fun hashCode(): Int {
        var result = isLoading.hashCode()
        result = 31 * result + showExitConfirmationDialog.hashCode()
        result = 31 * result + isAddingExercise.hashCode()
        result = 31 * result + showCamera.hashCode()
        result = 31 * result + showCameraPermissionDialog.hashCode()
        result = 31 * result + cameraPermissionGranted.hashCode()
        result = 31 * result + (targetExerciseIndex ?: 0)
        result = 31 * result + showImageSourceDialog.hashCode()
        result = 31 * result + showPhotoPreviewDialog.hashCode()
        result = 31 * result + (errorMessage?.hashCode() ?: 0)
        result = 31 * result + workout.hashCode()
        result = 31 * result + newExercise.hashCode()
        result = 31 * result + (capturedPhotoPreview?.contentHashCode() ?: 0)
        result = 31 * result + isEditMode.hashCode()
        result = 31 * result + (originalWorkout?.hashCode() ?: 0)
        return result
    }
}
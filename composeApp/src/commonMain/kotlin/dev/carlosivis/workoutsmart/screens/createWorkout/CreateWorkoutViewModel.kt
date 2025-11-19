package dev.carlosivis.workoutsmart.screens.createWorkout

import androidx.lifecycle.ViewModel
import dev.carlosivis.workoutsmart.models.ExerciseModel
import dev.carlosivis.workoutsmart.models.WorkoutModel
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
            is CreateWorkoutViewAction.GetExercises -> TODO("show exercises from others workout to choose")
            is CreateWorkoutViewAction.NavigateBack -> onNavigateBack()
            is CreateWorkoutViewAction.AddName -> addName(action.name)
            is CreateWorkoutViewAction.AddDescription -> addDescription(action.description)
            is CreateWorkoutViewAction.UpdateExercise -> updateExercise(action.index, action.exercise)
            is CreateWorkoutViewAction.StartAddingExercise -> startAddingExercise()
            is CreateWorkoutViewAction.CancelAddingExercise -> cancelAddingExercise()
            is CreateWorkoutViewAction.UpdateNewExercise -> updateNewExercise(action.exercise)
            is CreateWorkoutViewAction.ConfirmNewExercise -> confirmNewExercise()
            is CreateWorkoutViewAction.AttemptToNavigateBack -> attemptToNavigateBack()
            is CreateWorkoutViewAction.CancelNavigateBack -> cancelNavigateBack()
            is CreateWorkoutViewAction.UpdateNewExerciseImage -> updateNewExerciseImage(action.image)
            is CreateWorkoutViewAction.UpdateExistingExerciseImage -> updateExistingExerciseImage(action.index, action.image)

            is CreateWorkoutViewAction.RequestImageSource -> requestImageSource(action.exerciseIndex)
            is CreateWorkoutViewAction.ToggleImageSourceDialog -> toggleImageSourceDialog()
            is CreateWorkoutViewAction.SelectGallery -> selectGallery()
            is CreateWorkoutViewAction.RequestCameraAccess -> requestCameraAccess(action.exerciseIndex)
            is CreateWorkoutViewAction.ToggleCameraPermissionDialog -> toggleCameraPermissionDialog()
            is CreateWorkoutViewAction.GrantCameraPermission -> grantCameraPermission()
            is CreateWorkoutViewAction.DenyCameraPermission -> denyCameraPermission()
            is CreateWorkoutViewAction.ToggleCamera -> toggleCamera()
            is CreateWorkoutViewAction.OnCameraCapture -> onCameraCapture(action.image)
            is CreateWorkoutViewAction.ConfirmCapturedPhoto -> confirmCapturedPhoto()
            is CreateWorkoutViewAction.RetakeCapturedPhoto -> retakeCapturedPhoto()
            is CreateWorkoutViewAction.OnGalleryImageSelected -> onGalleryImageSelected(action.image)
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
            if (mutableExercises.indices.contains(index) ) { 
                mutableExercises[index] = exercise 
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

    private fun attemptToNavigateBack() {
        if (_state.value.isAddingExercise) {
            _state.update { it.copy(showExitConfirmationDialog = true) }
        }
        else if (_state.value.workout != WorkoutModel.empty()) {
            _state.update { it.copy(showExitConfirmationDialog = true)}
        }
        else {
            onNavigateBack()
        }
    }
    private fun cancelNavigateBack() {
        _state.update { it.copy(showExitConfirmationDialog = false) }
    }

    private fun updateNewExerciseImage(image: ByteArray) {
        _state.update {
            it.copy(newExercise = it.newExercise.copy(image = image))
        }
    }

    private fun updateExistingExerciseImage(index: Int, image: ByteArray) {
        _state.update {
            val mutableExercises = it.workout.exercises.toMutableList()
            if (mutableExercises.indices.contains(index)) {
                val updatedExercise = mutableExercises[index].copy(image = image)
                mutableExercises[index] = updatedExercise
            }
            it.copy(workout = it.workout.copy(exercises = mutableExercises))
        }
    }


    private fun grantCameraPermission() {
        _state.update { it.copy(
            cameraPermissionGranted = true,
            showCameraPermissionDialog = false,
            showCamera = true
        ) }
    }

    private fun denyCameraPermission() {
        _state.update { it.copy(
            cameraPermissionGranted = false,
            showCameraPermissionDialog = false,
            targetExerciseIndex = null
        ) }
    }

    private fun requestImageSource(exerciseIndex: Int?) {
        _state.update { it.copy(
            showImageSourceDialog = true,
            targetExerciseIndex = exerciseIndex
        ) }
    }

    private fun toggleImageSourceDialog() {
        _state.update { it.copy(showImageSourceDialog = !it.showImageSourceDialog) }
    }

    private fun selectGallery() {
        _state.update { it.copy(showImageSourceDialog = false) }
    }

    private fun requestCameraAccess(exerciseIndex: Int?) {
        _state.update { it.copy(
            showImageSourceDialog = false,
            targetExerciseIndex = exerciseIndex,
            showCameraPermissionDialog = !_state.value.cameraPermissionGranted
        ) }
    }

    private fun toggleCameraPermissionDialog() {
        _state.update { it.copy(showCameraPermissionDialog = !it.showCameraPermissionDialog) }
    }

    private fun toggleCamera() {
        _state.update {
            if (it.showCamera) {
                it.copy(
                    showCamera = false,
                    capturedPhotoPreview = null,
                    showPhotoPreviewDialog = false
                )
            } else {
                // Abrindo câmera
                it.copy(showCamera = true)
            }
        }
    }

    private fun onCameraCapture(image: ByteArray) {
        _state.update { currentState ->
            currentState.copy(
                capturedPhotoPreview = image,
                showPhotoPreviewDialog = true,
                showCamera = false
            )
        }
    }

    private fun confirmCapturedPhoto() {
        _state.update { currentState ->
            val image = currentState.capturedPhotoPreview ?: return@update currentState
            val targetIndex = currentState.targetExerciseIndex

            val newState = currentState.copy(
                showCamera = false,
                showImageSourceDialog = false,
                showPhotoPreviewDialog = false,
                capturedPhotoPreview = null
            )

            return@update if (targetIndex == null) {
                // Novo exercício
                newState.copy(newExercise = currentState.newExercise.copy(image = image))
            } else {
                // Exercício existente
                val mutableExercises = currentState.workout.exercises.toMutableList()
                if (mutableExercises.indices.contains(targetIndex)) {
                    val updatedExercise = mutableExercises[targetIndex].copy(image = image)
                    mutableExercises[targetIndex] = updatedExercise
                }
                newState.copy(workout = currentState.workout.copy(exercises = mutableExercises))
            }
        }
    }

    private fun retakeCapturedPhoto() {
        _state.update { it.copy(
            capturedPhotoPreview = null,
            showPhotoPreviewDialog = false,
            showCamera = true
        ) }
    }

    private fun onGalleryImageSelected(image: ByteArray) {
        _state.update { currentState ->
            val targetIndex = currentState.targetExerciseIndex

            if (targetIndex == null) {
                // Novo exercício
                currentState.copy(newExercise = currentState.newExercise.copy(image = image))
            } else {
                // Exercício existente
                val mutableExercises = currentState.workout.exercises.toMutableList()
                if (mutableExercises.indices.contains(targetIndex)) {
                    val updatedExercise = mutableExercises[targetIndex].copy(image = image)
                    mutableExercises[targetIndex] = updatedExercise
                }
                currentState.copy(workout = currentState.workout.copy(exercises = mutableExercises))
            }
        }
    }
}
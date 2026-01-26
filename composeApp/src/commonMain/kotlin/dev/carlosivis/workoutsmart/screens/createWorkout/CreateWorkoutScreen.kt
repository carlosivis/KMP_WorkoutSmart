package dev.carlosivis.workoutsmart.screens.createWorkout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import coil3.compose.AsyncImage
import com.preat.peekaboo.image.picker.SelectionMode
import com.preat.peekaboo.image.picker.rememberImagePickerLauncher
import com.preat.peekaboo.ui.camera.rememberPeekabooCameraState
import dev.carlosivis.workoutsmart.Utils.Dimens
import dev.carlosivis.workoutsmart.Utils.FontSizes
import dev.carlosivis.workoutsmart.Utils.Shapes
import dev.carlosivis.workoutsmart.Utils.WorkoutsSmartTheme
import dev.carlosivis.workoutsmart.composeResources.Res
import dev.carlosivis.workoutsmart.composeResources.action_allow
import dev.carlosivis.workoutsmart.composeResources.action_back
import dev.carlosivis.workoutsmart.composeResources.action_cancel
import dev.carlosivis.workoutsmart.composeResources.action_confirm
import dev.carlosivis.workoutsmart.composeResources.action_deny
import dev.carlosivis.workoutsmart.composeResources.add_exercise_button
import dev.carlosivis.workoutsmart.composeResources.add_photo_button
import dev.carlosivis.workoutsmart.composeResources.camera_button
import dev.carlosivis.workoutsmart.composeResources.camera_permission_message
import dev.carlosivis.workoutsmart.composeResources.camera_permission_title
import dev.carlosivis.workoutsmart.composeResources.create_workout_screen_title
import dev.carlosivis.workoutsmart.composeResources.edit_workout_screen_title
import dev.carlosivis.workoutsmart.composeResources.exercise_name_label
import dev.carlosivis.workoutsmart.composeResources.exercise_notes_label
import dev.carlosivis.workoutsmart.composeResources.exercise_photo_description
import dev.carlosivis.workoutsmart.composeResources.exercise_repetitions_label
import dev.carlosivis.workoutsmart.composeResources.exercise_series_label
import dev.carlosivis.workoutsmart.composeResources.exit_unsaved_changes_message
import dev.carlosivis.workoutsmart.composeResources.exit_without_saving_title
import dev.carlosivis.workoutsmart.composeResources.gallery_button
import dev.carlosivis.workoutsmart.composeResources.save_workout_button
import dev.carlosivis.workoutsmart.composeResources.select_image_source
import dev.carlosivis.workoutsmart.composeResources.workout_description_label
import dev.carlosivis.workoutsmart.composeResources.workout_title_label
import dev.carlosivis.workoutsmart.models.ExerciseModel
import dev.carlosivis.workoutsmart.screens.components.CameraCaptureScreen
import dev.carlosivis.workoutsmart.screens.components.CustomDialog
import dev.carlosivis.workoutsmart.screens.components.PhotoPreviewDialog
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateWorkoutScreen(
    viewModel: CreateWorkoutViewModel
) {
    val state by viewModel.state.collectAsState()
    val action: (CreateWorkoutViewAction) -> Unit = viewModel::dispatchAction
    val scope = rememberCoroutineScope()

    val imagePickerLauncher = rememberImagePickerLauncher(
        selectionMode = SelectionMode.Single,
        scope = scope,
        onResult = { byteArrayList ->
            byteArrayList.firstOrNull()?.let {
                action(CreateWorkoutViewAction.OnGalleryImageSelected(it))
            }
        }
    )
    Content(
        modifier = Modifier.padding(horizontal = Dimens.Medium),
        state = state,
        action = action,
        imagePickerLauncher = {
            action(CreateWorkoutViewAction.SelectGallery)
            imagePickerLauncher.launch()
        }
    )
}

@Composable
private fun Content(
    modifier: Modifier = Modifier,
    state: CreateWorkoutViewState,
    action: (CreateWorkoutViewAction) -> Unit,
    imagePickerLauncher: () -> Unit
) {

    if (state.showImageSourceDialog) {
        ImageSourceDialog(
            onSelectCamera = {
                action(CreateWorkoutViewAction.RequestCameraAccess(state.targetExerciseIndex))
            },
            onSelectGallery = {
                imagePickerLauncher()
            },
            onDismiss = { action(CreateWorkoutViewAction.ToggleImageSourceDialog) }
        )
    }

    if (state.showCameraPermissionDialog && !state.cameraPermissionGranted) {
        CameraPermissionDialog(
            onGrantPermission = { action(CreateWorkoutViewAction.GrantCameraPermission) },
            onDenyPermission = { action(CreateWorkoutViewAction.DenyCameraPermission) },
            onDismiss = { action(CreateWorkoutViewAction.ToggleCameraPermissionDialog) }
        )
    }

    if (state.showCamera) {
        CameraCaptureScreen(
            state = rememberPeekabooCameraState(
                onCapture = { byteArray ->
                    byteArray?.let {
                        action(CreateWorkoutViewAction.OnCameraCapture(it))
                    }
                }
            ),
            onDismiss = { action(CreateWorkoutViewAction.ToggleCamera) },
        )
        return
    }

    if (state.showPhotoPreviewDialog && state.capturedPhotoPreview != null) {
        PhotoPreviewDialog(
            photoByteArray = state.capturedPhotoPreview,
            onConfirm = { action(CreateWorkoutViewAction.ConfirmCapturedPhoto) },
            onRetake = { action(CreateWorkoutViewAction.RetakeCapturedPhoto) }
        )
        return
    }

    if (state.showExitConfirmationDialog) {
        CustomDialog(
            title = stringResource(Res.string.exit_without_saving_title),
            message = stringResource(Res.string.exit_unsaved_changes_message),
            onConfirm = { action(CreateWorkoutViewAction.NavigateBack) },
            onCancel = { action(CreateWorkoutViewAction.CancelNavigateBack) }
        )
    }

    Scaffold(modifier = modifier) { paddingValues ->

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = { action(CreateWorkoutViewAction.AttemptToNavigateBack) },
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(Res.string.action_back)
                    )
                }
                Text(
                    text = if (state.isEditMode) stringResource(Res.string.edit_workout_screen_title) else stringResource(
                        Res.string.create_workout_screen_title
                    ),
                    fontSize = FontSizes.TitleMedium,
                    textAlign = TextAlign.Center
                )
            }

            TextField(
                value = state.workout.name,
                onValueChange = { name ->
                    action(CreateWorkoutViewAction.AddName(name))
                },
                label = { Text(stringResource(Res.string.workout_title_label)) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
            )

            Spacer(modifier = Modifier.height(Dimens.Medium))

            TextField(
                value = state.workout.description,
                onValueChange = { description ->
                    action(CreateWorkoutViewAction.AddDescription(description))
                },
                label = { Text(stringResource(Res.string.workout_description_label)) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
            )

            Spacer(modifier = Modifier.height(Dimens.Medium))

            if (state.isAddingExercise) {
                NewExerciseCard(
                    exercise = state.newExercise,
                    onExerciseChange = { action(CreateWorkoutViewAction.UpdateNewExercise(it)) },
                    onConfirm = { action(CreateWorkoutViewAction.ConfirmNewExercise) },
                    onCancel = { action(CreateWorkoutViewAction.CancelAddingExercise) },
                    onAddPhotoClick = { action(CreateWorkoutViewAction.RequestImageSource(null)) }
                )
            } else {
                Button(onClick = { action(CreateWorkoutViewAction.StartAddingExercise) }) {
                    Text(stringResource(Res.string.add_exercise_button))
                }
            }

            Spacer(modifier = Modifier.height(Dimens.Medium))

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(Dimens.Medium)
            ) {
                itemsIndexed(state.workout.exercises) { index, exercise ->
                    ExerciseInput(
                        exercise = exercise,
                        onExerciseChange = { updatedExercise ->
                            action(CreateWorkoutViewAction.UpdateExercise(index, updatedExercise))
                        },
                        onAddPhotoClick = { action(CreateWorkoutViewAction.RequestImageSource(index)) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(Dimens.Medium))

            Button(
                onClick = { action(CreateWorkoutViewAction.SaveWorkout) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(Res.string.save_workout_button))
            }
        }
    }
}

@Composable
private fun NewExerciseCard(
    exercise: ExerciseModel,
    onExerciseChange: (ExerciseModel) -> Unit,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    onAddPhotoClick: () -> Unit
) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = Dimens.Large)
    ) {
        Column(modifier = Modifier.padding(Dimens.Medium)) {
            ExerciseInput(
                exercise = exercise,
                onExerciseChange = onExerciseChange,
                onAddPhotoClick = onAddPhotoClick
            )
            Spacer(modifier = Modifier.height(Dimens.Medium))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                OutlinedButton(onClick = onCancel) {
                    Text(stringResource(Res.string.action_cancel))
                }
                Spacer(modifier = Modifier.width(Dimens.Small))
                Button(onClick = onConfirm) {
                    Text(stringResource(Res.string.action_confirm))
                }
            }
        }
    }
}

@Composable
private fun ExerciseInput(
    exercise: ExerciseModel,
    onExerciseChange: (ExerciseModel) -> Unit,
    onAddPhotoClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(0.9f)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(Dimens.Medium)) {
            TextField(
                value = exercise.name,
                onValueChange = { onExerciseChange(exercise.copy(name = it)) },
                label = { Text(stringResource(Res.string.exercise_name_label)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(Shapes.ExtraLarge),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    capitalization = KeyboardCapitalization.Sentences
                )
            )

            Spacer(modifier = Modifier.height(Dimens.Medium))

            TextField(
                value = exercise.notes,
                onValueChange = { onExerciseChange(exercise.copy(notes = it)) },
                label = { Text(stringResource(Res.string.exercise_notes_label)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(Shapes.ExtraLarge),
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
            )

            Spacer(modifier = Modifier.height(Dimens.Medium))

            Row(modifier = Modifier.fillMaxWidth()) {
                TextField(
                    value = if (exercise.series == 0) "" else exercise.series.toString(),
                    onValueChange = {
                        val series = it.toIntOrNull() ?: 0
                        onExerciseChange(exercise.copy(series = series))
                    },
                    label = { Text(stringResource(Res.string.exercise_series_label)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(Shapes.ExtraLarge)
                )

                Spacer(modifier = Modifier.width(Dimens.Small))

                TextField(
                    value = if (exercise.repetitions == 0) "" else exercise.repetitions.toString(),
                    onValueChange = {
                        val repetitions = it.toIntOrNull() ?: 0
                        onExerciseChange(exercise.copy(repetitions = repetitions))
                    },
                    label = { Text(stringResource(Res.string.exercise_repetitions_label)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(Shapes.ExtraLarge)
                )
            }

            Spacer(modifier = Modifier.height(Dimens.Large))

            ExerciseImageSection(
                image = exercise.image,
                onAddPhotoClick = onAddPhotoClick
            )
        }
    }
}

@Composable
private fun ExerciseImageSection(
    image: ByteArray?,
    onAddPhotoClick: () -> Unit
) {
    if (image != null) {
        AsyncImage(
            model = image,
            contentDescription = stringResource(Res.string.exercise_photo_description),
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f),
            contentScale = ContentScale.Crop
        )
    } else {
        Button(
            onClick = onAddPhotoClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(Res.string.add_photo_button))
        }
    }
}

@Composable
private fun ImageSourceDialog(
    onSelectCamera: () -> Unit,
    onSelectGallery: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        text = { Text(stringResource(Res.string.select_image_source)) },
        confirmButton = {
            TextButton(onClick = onSelectCamera) {
                Text(stringResource(Res.string.camera_button))
            }
        },
        dismissButton = {
            TextButton(onClick = onSelectGallery) {
                Text(stringResource(Res.string.gallery_button))
            }
        }
    )
}

@Composable
private fun CameraPermissionDialog(
    onGrantPermission: () -> Unit,
    onDenyPermission: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(Res.string.camera_permission_title)) },
        text = { Text(stringResource(Res.string.camera_permission_message)) },
        confirmButton = {
            Button(onClick = onGrantPermission) {
                Text(stringResource(Res.string.action_allow))
            }
        },
        dismissButton = {
            TextButton(onClick = onDenyPermission) {
                Text(stringResource(Res.string.action_deny))
            }
        }
    )
}

@Preview
@Composable
private fun ContentPreview() {
    WorkoutsSmartTheme {
        Content(
            state = CreateWorkoutViewState(),
            action = {},
            imagePickerLauncher = {}
        )
    }
}

@Preview
@Composable
private fun NewExerciseCardPreview() {
    WorkoutsSmartTheme {
        NewExerciseCard(
            exercise = ExerciseModel.empty(),
            onExerciseChange = {},
            onConfirm = {},
            onCancel = {},
            onAddPhotoClick = {}
        )
    }
}
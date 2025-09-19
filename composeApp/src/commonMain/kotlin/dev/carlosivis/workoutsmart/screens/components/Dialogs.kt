package dev.carlosivis.workoutsmart.screens.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import dev.carlosivis.workoutsmart.Utils.Dimens
import dev.carlosivis.workoutsmart.composeResources.Res
import dev.carlosivis.workoutsmart.composeResources.action_cancel
import dev.carlosivis.workoutsmart.composeResources.action_confirm
import dev.carlosivis.workoutsmart.composeResources.camera_option
import dev.carlosivis.workoutsmart.composeResources.choose_image_source_title
import dev.carlosivis.workoutsmart.composeResources.gallery_option
import dev.carlosivis.workoutsmart.platform.PermissionType
import dev.carlosivis.workoutsmart.platform.checkPermission
import dev.carlosivis.workoutsmart.platform.rememberPermissionLauncher
import org.jetbrains.compose.resources.stringResource


@Composable
fun CustomDialog(
    title: String,
    message: String?,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    Dialog(onDismissRequest = onCancel) {
        Card {
            Column(
                modifier = Modifier
                    .padding(Dimens.Medium),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(Dimens.Medium))
                Text(
                    text = message ?: "",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(Dimens.Medium))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Dimens.Medium, Alignment.CenterHorizontally)
                ) {
                    OutlinedButton(onClick = onCancel) {
                        Text(stringResource(Res.string.action_cancel))
                    }
                    Button(
                        onClick = onConfirm
                    ) {
                        Text(stringResource(Res.string.action_confirm))
                    }
                }
            }
        }
    }
}

@Composable
fun ImageSourceDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onPermissionResult: (Boolean, PermissionType) -> Unit,
    onLaunchCamera: () -> Unit,
    onLaunchGallery: () -> Unit
) {
    val permissionLauncher = rememberPermissionLauncher { isGranted, permissionType ->
        onPermissionResult(isGranted, permissionType)
    }

    val hasGalleryPermission = checkPermission(PermissionType.GALLERY)
    val hasCameraPermission = checkPermission(PermissionType.CAMERA)

    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(stringResource(Res.string.choose_image_source_title)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (hasGalleryPermission) {
                            onLaunchGallery()
                        } else {
                            permissionLauncher.launch(PermissionType.GALLERY)
                        }
                        onDismiss()
                    }
                ) {
                    Text(stringResource(Res.string.gallery_option))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        if (hasCameraPermission) {
                            onLaunchCamera()
                        } else {
                            permissionLauncher.launch(PermissionType.CAMERA)
                        }
                        onDismiss()
                    }
                ) {
                    Text(stringResource(Res.string.camera_option))
                }
            }
        )
    }
}
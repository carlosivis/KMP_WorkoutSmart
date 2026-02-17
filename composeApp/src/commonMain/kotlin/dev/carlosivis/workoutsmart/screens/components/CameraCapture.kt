package dev.carlosivis.workoutsmart.screens.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlipCameraAndroid
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import com.preat.peekaboo.ui.camera.PeekabooCamera
import com.preat.peekaboo.ui.camera.PeekabooCameraState
import dev.carlosivis.workoutsmart.utils.Dimens
import dev.carlosivis.workoutsmart.composeResources.Res
import dev.carlosivis.workoutsmart.composeResources.action_confirm
import dev.carlosivis.workoutsmart.composeResources.camera_capture_capture_photo_description
import dev.carlosivis.workoutsmart.composeResources.camera_capture_capture_photo_text
import dev.carlosivis.workoutsmart.composeResources.camera_capture_close_description
import dev.carlosivis.workoutsmart.composeResources.camera_capture_flip_camera_description
import dev.carlosivis.workoutsmart.composeResources.camera_permission_denied_button
import dev.carlosivis.workoutsmart.composeResources.photo_preview_image_description
import dev.carlosivis.workoutsmart.composeResources.photo_preview_retake_button
import org.jetbrains.compose.resources.stringResource


@Composable
fun CameraCaptureScreen(
    state: PeekabooCameraState,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(Dimens.ExtraLarge),
        contentAlignment = Alignment.Center
    ) {
        PeekabooCamera(
            state = state,
            modifier = Modifier.fillMaxSize(),
            permissionDeniedContent = {
                PermissionDeniedContent(onDismiss = onDismiss)
            }
        )

        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(Dimens.Medium),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onDismiss) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(Res.string.camera_capture_close_description),
                    tint = Color.White
                )
            }

            IconButton(onClick = { state.toggleCamera() }) {
                Icon(
                    imageVector = Icons.Default.FlipCameraAndroid,
                    contentDescription = stringResource(Res.string.camera_capture_flip_camera_description),
                    tint = Color.White
                )
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(Dimens.Large)
        ) {
            Button(
                onClick = { state.capture() },
                modifier = Modifier
            ) {
                Icon(
                    imageVector = Icons.Default.Camera,
                    contentDescription = stringResource(Res.string.camera_capture_capture_photo_description),
                    tint = Color.White,
                    modifier = Modifier.padding(end = Dimens.Small)
                )
                Text(stringResource(Res.string.camera_capture_capture_photo_text))
            }
        }
    }
}


@Composable
fun PhotoPreviewDialog(
    photoByteArray: ByteArray,
    onConfirm: () -> Unit,
    onRetake: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Preview da imagem
            AsyncImage(
                model = photoByteArray,
                contentDescription = stringResource(Res.string.photo_preview_image_description),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentScale = ContentScale.Crop
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimens.Medium),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                TextButton(
                    onClick = onRetake,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(Res.string.photo_preview_retake_button), color = Color.White)
                }
                TextButton(
                    onClick = onConfirm,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(Res.string.action_confirm), color = Color.Green)
                }
            }
        }
    }
}

@Composable
private fun PermissionDeniedContent(onDismiss: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Button(onClick = onDismiss) {
            Text(stringResource(Res.string.camera_permission_denied_button))
        }
    }
}

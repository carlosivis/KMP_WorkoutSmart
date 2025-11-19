package dev.carlosivis.workoutsmart.screens.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.preat.peekaboo.ui.camera.PeekabooCameraState

/**
 * Interface expect/actual para tratamento de câmera específico de plataforma
 * Android e iOS têm diferentes requirements para câmera
 */
expect class CameraPermissionHandler {
    fun requestCameraPermission(onResult: (Boolean) -> Unit)
    fun hasCameraPermission(): Boolean
}

/**
 * Composable expect para tratamento específico de câmera por plataforma
 */
@Composable
expect fun PlatformCameraView(
    state: PeekabooCameraState,
    modifier: Modifier = Modifier,
    onPermissionDenied: () -> Unit = {}
)


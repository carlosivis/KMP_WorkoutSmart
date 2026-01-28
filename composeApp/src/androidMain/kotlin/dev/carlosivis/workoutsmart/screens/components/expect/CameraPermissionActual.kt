package dev.carlosivis.workoutsmart.screens.components.expect

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.preat.peekaboo.ui.camera.PeekabooCamera
import com.preat.peekaboo.ui.camera.PeekabooCameraState

/**
 * Implementação Android para gerenciamento de permissões de câmera
 * Usa ActivityCompat para requisitar permissões em runtime
 */
actual class CameraPermissionHandler(private val context: Context) {

    actual fun requestCameraPermission(onResult: (Boolean) -> Unit) {
        val permission = Manifest.permission.CAMERA
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED

        onResult(hasPermission)
    }

    actual fun hasCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }
}

/**
 * Implementação Android do visualizador de câmera
 * Usa PeekabooCamera natively com suporte completo a CameraX
 */
@Composable
actual fun PlatformCameraView(
    state: PeekabooCameraState,
    modifier: Modifier,
    onPermissionDenied: () -> Unit
) {
    PeekabooCamera(
        state = state,
        modifier = modifier,
        permissionDeniedContent = {
            onPermissionDenied()
        }
    )
}


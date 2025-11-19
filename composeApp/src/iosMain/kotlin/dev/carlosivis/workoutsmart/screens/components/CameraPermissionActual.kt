package dev.carlosivis.workoutsmart.screens.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.preat.peekaboo.ui.camera.PeekabooCamera
import com.preat.peekaboo.ui.camera.PeekabooCameraState

/**
 * Implementação iOS para gerenciamento de permissões de câmera
 * iOS requer NSCameraUsageDescription no Info.plist
 * Permissões gerenciadas via AVCaptureDevice.requestAccess()
 */
actual class CameraPermissionHandler {

    actual fun requestCameraPermission(onResult: (Boolean) -> Unit) {
        // No iOS, as permissões são solicitadas automaticamente pelo Peekaboo
        // quando a câmera é acessada pela primeira vez
        // Esta função é chamada apenas para verificar estado
        onResult(true) // iOS pede permissão automaticamente
    }

    actual fun hasCameraPermission(): Boolean {
        // iOS: verificação seria feita através de AVCaptureDevice
        // Por enquanto, retornamos true e deixamos Peekaboo gerenciar
        return true
    }
}

/**
 * Implementação iOS do visualizador de câmera
 * Usa PeekabooCamera com suporte nativo a AVFoundation
 *
 * REQUER NO Info.plist:
 * <key>NSCameraUsageDescription</key>
 * <string>We need camera access to capture exercise photos</string>
 *
 * <key>NSPhotoLibraryUsageDescription</key>
 * <string>We need access to your photo library</string>
 */
@Composable
actual fun PlatformCameraView(
    state: PeekabooCameraState,
    modifier: Modifier,
    onPermissionDenied: () -> Unit
) {
    Box(
        modifier = modifier.fillMaxSize().background(Color.Black)
    ) {
        PeekabooCamera(
            state = state,
            modifier = Modifier.fillMaxSize(),
            permissionDeniedContent = {
                iOSCameraPermissionDeniedContent(onPermissionDenied)
            }
        )
    }
}

@Composable
private fun iOSCameraPermissionDeniedContent(onDismiss: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Button(onClick = onDismiss) {
            Text("Camera permission denied. Please enable in Settings.")
        }
    }
}


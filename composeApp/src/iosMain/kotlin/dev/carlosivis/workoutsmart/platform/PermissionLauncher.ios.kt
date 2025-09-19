package dev.carlosivis.workoutsmart.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.AVFoundation.AVAuthorizationStatusAuthorized
import platform.AVFoundation.AVCaptureDevice
import platform.AVFoundation.AVMediaTypeVideo
import platform.AVFoundation.authorizationStatusForMediaType
import platform.AVFoundation.requestAccessForMediaType
import platform.Foundation.NSURL
import platform.Photos.PHAuthorizationStatusAuthorized
import platform.Photos.PHPhotoLibrary
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationOpenSettingsURLString

@Composable
actual fun rememberPermissionLauncher(
    onResult: (isGranted: Boolean, permission: PermissionType) -> Unit
): PermissionLauncher {
    return remember {
        object : PermissionLauncher {
            override fun launch(permission: PermissionType) {
                when (permission) {
                    PermissionType.CAMERA -> {
                        AVCaptureDevice.requestAccessForMediaType(AVMediaTypeVideo) { isGranted ->
                            onResult(isGranted, permission)
                            if (!isGranted) {
                                redirectToSettings()
                            }
                        }
                    }
                    PermissionType.GALLERY -> {
                        PHPhotoLibrary.requestAuthorization { status ->
                            val isGranted = status == PHAuthorizationStatusAuthorized
                            onResult(isGranted, permission)
                            if (!isGranted) {
                                redirectToSettings()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
actual fun checkPermission(permission: PermissionType): Boolean {
    return when (permission) {
        PermissionType.CAMERA -> {
            AVCaptureDevice.authorizationStatusForMediaType(AVMediaTypeVideo) == AVAuthorizationStatusAuthorized
        }
        PermissionType.GALLERY -> {
            PHPhotoLibrary.authorizationStatus() == PHAuthorizationStatusAuthorized
        }
    }
}

// Helper para guiar o usuário às configurações se ele negar a permissão permanentemente.
private fun redirectToSettings() {
    val settingsUrl = NSURL.URLWithString(UIApplicationOpenSettingsURLString)
    settingsUrl?.let {
        if (UIApplication.sharedApplication.canOpenURL(it)) {
            UIApplication.sharedApplication.openURL(it)
        }
    }
}
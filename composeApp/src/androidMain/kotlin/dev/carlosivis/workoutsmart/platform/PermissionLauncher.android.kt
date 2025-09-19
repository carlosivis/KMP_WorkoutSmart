package dev.carlosivis.workoutsmart.platform

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

@Composable
actual fun rememberPermissionLauncher(
    onResult: (isGranted: Boolean, permission: PermissionType) -> Unit
): PermissionLauncher {
    var permissionToRequest: PermissionType? = null

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            permissionToRequest?.let {
                onResult(isGranted, it)
            }
        }
    )

    return remember {
        object : PermissionLauncher {
            override fun launch(permission: PermissionType) {
                permissionToRequest = permission
                val androidPermission = permission.toAndroidPermission()
                launcher.launch(androidPermission)
            }
        }
    }
}

@Composable
actual fun checkPermission(permission: PermissionType): Boolean {
    val context = LocalContext.current
    return ContextCompat.checkSelfPermission(
        context,
        permission.toAndroidPermission()
    ) == PackageManager.PERMISSION_GRANTED
}

private fun PermissionType.toAndroidPermission(): String {
    return when (this) {
        PermissionType.CAMERA -> Manifest.permission.CAMERA
        PermissionType.GALLERY -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Manifest.permission.READ_MEDIA_IMAGES
            } else {
                Manifest.permission.READ_EXTERNAL_STORAGE
            }
        }
    }
}

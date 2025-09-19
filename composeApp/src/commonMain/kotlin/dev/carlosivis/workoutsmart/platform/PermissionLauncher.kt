package dev.carlosivis.workoutsmart.platform

import androidx.compose.runtime.Composable

enum class PermissionType {
    CAMERA,
    GALLERY
}

interface PermissionLauncher {
    fun launch(permission: PermissionType)
}

@Composable
expect fun rememberPermissionLauncher(
    onResult: (isGranted: Boolean, permission: PermissionType) -> Unit
): PermissionLauncher

@Composable
expect fun checkPermission(permission: PermissionType): Boolean
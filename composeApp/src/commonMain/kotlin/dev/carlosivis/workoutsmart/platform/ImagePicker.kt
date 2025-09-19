package dev.carlosivis.workoutsmart.platform

import androidx.compose.runtime.Composable

expect class ImagePicker(
    onImageSelected: (ByteArray) -> Unit,
    onDismiss: () -> Unit
) {
    @Composable
    fun GalleryPicker()

    @Composable
    fun CameraPicker()
}
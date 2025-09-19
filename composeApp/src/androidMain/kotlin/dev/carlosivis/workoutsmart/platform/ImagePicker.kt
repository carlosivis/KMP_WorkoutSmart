package dev.carlosivis.workoutsmart.platform

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import java.io.ByteArrayOutputStream
import java.io.File

actual class ImagePicker actual constructor(
    private val onImageSelected: (ByteArray) -> Unit,
    private val onDismiss: () -> Unit
) {
    @Composable
    actual fun GalleryPicker() {
        val context = LocalContext.current
        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            if (uri != null) {
                onImageSelected(uri.toByteArray(context.contentResolver))
            } else {
                onDismiss()
            }
        }

        LaunchedEffect(Unit) {
            launcher.launch("image/*")
        }
    }

    @Composable
    actual fun CameraPicker() {
        val context = LocalContext.current
        val tempUri = context.createTempImageUri()

        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.TakePicture()
        ) { success ->
            if (success) {
                onImageSelected(tempUri.toByteArray(context.contentResolver))
            } else {
                onDismiss()
            }
        }

        LaunchedEffect(Unit) {
            launcher.launch(tempUri)
        }
    }
}

private fun Context.createTempImageUri(): Uri {
    val tempFile = File.createTempFile("temp_image", ".jpg", cacheDir).apply {
        createNewFile()
        deleteOnExit()
    }
    return FileProvider.getUriForFile(
        this,
        "${applicationContext.packageName}.provider",
        tempFile
    )
}


private fun Uri.toByteArray(contentResolver: ContentResolver): ByteArray {
    val inputStream = contentResolver.openInputStream(this)
    val outputStream = ByteArrayOutputStream()
    inputStream?.use {
        it.copyTo(outputStream)
    }
    return outputStream.toByteArray()
}

private fun Bitmap.toByteArray(): ByteArray {
    val outputStream = ByteArrayOutputStream()
    this.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
    return outputStream.toByteArray()
}

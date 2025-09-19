package dev.carlosivis.workoutsmart.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.interop.LocalUIViewController
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.refTo
import platform.Foundation.NSData
import platform.posix.memcpy
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation
import platform.UIKit.UIImagePickerController
import platform.UIKit.UIImagePickerControllerDelegateProtocol
import platform.UIKit.UIImagePickerControllerOriginalImage
import platform.UIKit.UIImagePickerControllerSourceType
import platform.UIKit.UINavigationControllerDelegateProtocol
import platform.darwin.NSObject

@OptIn(ExperimentalForeignApi::class)
fun NSData.toByteArray(): ByteArray {
    val size = length.toInt()
    val bytes = ByteArray(size)
    if (size > 0) {
        memcpy(bytes.refTo(0), this.bytes, this.length)
    }
    return bytes
}

private class ImagePickerDelegate(
    var onImageSelected: (ByteArray) -> Unit,
    var onDismiss: () -> Unit
) : NSObject(), UIImagePickerControllerDelegateProtocol, UINavigationControllerDelegateProtocol {

    override fun imagePickerController(picker: UIImagePickerController, didFinishPickingMediaWithInfo: Map<Any?, *>) {
        val image = didFinishPickingMediaWithInfo[UIImagePickerControllerOriginalImage] as? UIImage
        if (image != null) {
            val imageData = UIImageJPEGRepresentation(image, 0.8) // 80% compression
            if (imageData != null) {
                onImageSelected(imageData.toByteArray())
            }
        }
        picker.dismissViewControllerAnimated(true, null)
    }

    override fun imagePickerControllerDidCancel(picker: UIImagePickerController) {
        onDismiss()
        picker.dismissViewControllerAnimated(true, null)
    }
}

actual class ImagePicker actual constructor(
    private val onImageSelected: (ByteArray) -> Unit,
    private val onDismiss: () -> Unit,
) {

    @Composable
    actual fun GalleryPicker() {
        val uiViewController = LocalUIViewController.current
        val delegate = remember {
            ImagePickerDelegate(
                onImageSelected = onImageSelected,
                onDismiss = onDismiss
            )
        }
        delegate.onImageSelected = onImageSelected
        delegate.onDismiss = onDismiss


        LaunchedEffect(Unit) {
            val picker = UIImagePickerController()
            picker.sourceType = UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypePhotoLibrary
            picker.allowsEditing = false
            picker.delegate = delegate
            uiViewController.presentViewController(picker, animated = true, completion = null)
        }
    }

    @Composable
    actual fun CameraPicker() {
        val uiViewController = LocalUIViewController.current
        val delegate = remember {
            ImagePickerDelegate(
                onImageSelected = onImageSelected,
                onDismiss = onDismiss
            )
        }
        delegate.onImageSelected = onImageSelected
        delegate.onDismiss = onDismiss

        LaunchedEffect(Unit) {
            val picker = UIImagePickerController()
            picker.sourceType = UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypeCamera
            picker.allowsEditing = false
            picker.delegate = delegate
            uiViewController.presentViewController(picker, animated = true, completion = null)
        }
    }
}

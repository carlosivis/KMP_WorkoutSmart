package dev.carlosivis.workoutsmart.plataform

import androidx.compose.ui.platform.Clipboard
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIPasteboard

actual suspend fun Clipboard.copyText(text: String) {
    UIPasteboard.generalPasteboard.string = text
}

actual suspend fun shareText(text: String) {
    val controller = UIApplication.sharedApplication.keyWindow?.rootViewController
    val activityController = UIActivityViewController(listOf(text), null)
    controller?.presentViewController(activityController, true, null)
}
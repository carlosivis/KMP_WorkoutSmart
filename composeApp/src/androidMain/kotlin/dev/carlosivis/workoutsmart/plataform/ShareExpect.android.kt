package dev.carlosivis.workoutsmart.plataform

import android.content.ClipData
import android.content.Context
import android.content.Intent
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.Clipboard
import org.koin.mp.KoinPlatformTools

actual suspend fun Clipboard.copyText(text: String) {
    val clipEntry = ClipEntry(ClipData.newPlainText("label", text))
    setClipEntry(clipEntry)
}

actual suspend fun shareText(text: String) {
    val context: Context = KoinPlatformTools.defaultContext().get().get()

    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, text)
        type = "text/plain"
    }

    val shareIntent = Intent.createChooser(sendIntent, null).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }

    context.startActivity(shareIntent)
}
package utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream
import java.io.File

actual class ImageSerializer(private val context: Context) {

    actual fun imageToByteArray(imagePath: String): ByteArray? {
        return try {
            val bitmap = BitmapFactory.decodeFile(imagePath)
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
            outputStream.toByteArray()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    actual fun byteArrayToFile(byteArray: ByteArray, fileName: String): Boolean {
        return try {
            val file = File(context.filesDir, fileName)
            file.writeBytes(byteArray)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}


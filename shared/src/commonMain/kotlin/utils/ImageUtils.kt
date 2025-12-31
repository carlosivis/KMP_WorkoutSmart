package utils

import kotlinx.io.files.Path

expect class ImageSerializer {
    fun imageToByteArray(imagePath: String): ByteArray?
    fun byteArrayToFile(byteArray: ByteArray, fileName: String): Boolean
}

fun ByteArray.toBase64String(): String {
    return this.joinToString("") { "%02x".format(it) }
}

fun String.fromBase64String(): ByteArray {
    return this.chunked(2).map { it.toInt(16).toByte() }.toByteArray()
}


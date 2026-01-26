package dev.carlosivis.workoutsmart.Utils

import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
fun formatDateToString(date: Long): String{
    val instant = Instant.fromEpochSeconds(date)
    val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    val formattedDate = "${localDateTime.day.toString().padStart(2, '0')}/"
        .plus("${localDateTime.month.number.toString().padStart(2, '0')}/")
        .plus("${localDateTime.year} ")
        .plus("${localDateTime.hour.toString().padStart(2, '0')}:")
        .plus(localDateTime.minute.toString().padStart(2, '0'))
    return formattedDate
}

fun Int.format(): String {
    return if (this < 10) "0$this" else this.toString()
}

fun formatDuration(seconds: Long): String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val remainingSeconds = seconds % 60

    return buildString {
        if (hours > 0) append("${hours}h ")
        if (minutes > 0 || hours > 0) append("${minutes}m ")
        append("${remainingSeconds}s")
    }
}
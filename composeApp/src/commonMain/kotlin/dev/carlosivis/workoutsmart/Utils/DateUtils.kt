package dev.carlosivis.workoutsmart.Utils

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

fun formatDateToString(date: Long): String{
    val instant = Instant.fromEpochSeconds(date)
    val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    val formattedDate = "${localDateTime.dayOfMonth.toString().padStart(2, '0')}/"
        .plus("${localDateTime.monthNumber.toString().padStart(2, '0')}/")
        .plus("${localDateTime.year} ")
        .plus("${localDateTime.hour.toString().padStart(2, '0')}:")
        .plus(localDateTime.minute.toString().padStart(2, '0'))
    return formattedDate
}
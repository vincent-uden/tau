package com.uden.tau

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.time.Duration

enum class DateFormat {
    Date,
    TimeShort,
}

fun formatInstant(format: DateFormat, date: Instant): String {
    val formatter = DateTimeFormatter.ofPattern(when (format) {
        DateFormat.Date -> "yyyy-MM-dd"
        DateFormat.TimeShort -> "HH:mm"
    }).withLocale(Locale.getDefault()).withZone(ZoneId.systemDefault())

    return formatter.format(date)
}
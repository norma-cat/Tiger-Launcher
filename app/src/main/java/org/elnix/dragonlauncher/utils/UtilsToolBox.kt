package org.elnix.dragonlauncher.utils

import androidx.compose.runtime.Composable
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter


@Composable
fun isValidTimeFormat(formatter: String): Boolean = try {
    val timeFormatter = DateTimeFormatter.ofPattern(formatter)
    val now = LocalTime.now()
    now.format(timeFormatter)
    true
} catch (e: Exception) {
    println("❌ Time format validation failed: '$formatter' -> ${e.message}")
    false
}

@Composable
fun isValidDateFormat(formatter: String): Boolean = try {
    val dateFormatter = DateTimeFormatter.ofPattern(formatter)
    val today = LocalDate.now()
    today.format(dateFormatter)
    true
} catch (e: Exception) {
    println("❌ Date format validation failed: '$formatter' -> ${e.message}")
    false
}

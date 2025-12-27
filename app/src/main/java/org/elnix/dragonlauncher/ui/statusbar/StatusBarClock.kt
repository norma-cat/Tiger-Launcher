package org.elnix.dragonlauncher.ui.statusbar

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.delay
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun StatusBarClock(
    showSeconds: Boolean,
    textColor: Color
) {
    val formatter = remember(showSeconds) {
        DateTimeFormatter.ofPattern(
            if (showSeconds) "HH:mm:ss" else "HH:mm"
        )
    }

    var time by remember { mutableStateOf(LocalTime.now()) }

    LaunchedEffect(showSeconds) {
        while (true) {
            time = LocalTime.now()
            delay(if (showSeconds) 1_000 else 60_000)
        }
    }

    Text(
        text = time.format(formatter),
        color = textColor,
        style = MaterialTheme.typography.bodyMedium
    )
}

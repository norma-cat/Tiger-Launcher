package org.elnix.dragonlauncher.ui.statusbar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun StatusBar(
    backgroundColor: Color,
    textColor: Color,
    showTime: Boolean,
    showSeconds: Boolean,
    showNotifications: Boolean,
    showBattery: Boolean,
    showConnectivity: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        if (showTime) {
            StatusBarClock(showSeconds, textColor)
        }

        Spacer(modifier = Modifier.weight(1f))

        if (showNotifications) {
            StatusBarNotifications()
            Spacer(modifier = Modifier.width(6.dp))
        }

        if (showConnectivity) {
            StatusBarConnectivity(textColor)
            Spacer(modifier = Modifier.width(6.dp))
        }

        if (showBattery) {
            StatusBarBattery(textColor)
        }
    }
}

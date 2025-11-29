package org.elnix.dragonlauncher.ui.welcome

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun WelcomePageLauncher() {
    val ctx = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Set as Default Launcher",
            color = Color.White,
            fontSize = 24.sp
        )

        Spacer(Modifier.height(32.dp))

        Button(
            onClick = {
                ctx.startActivity(Intent(Settings.ACTION_HOME_SETTINGS))
            }
        ) {
            Text("Open Default Launcher Settings")
        }
    }
}

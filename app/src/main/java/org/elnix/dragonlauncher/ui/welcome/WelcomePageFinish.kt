package org.elnix.dragonlauncher.ui.welcome

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun WelcomePageFinish(
    onEnterSettings: () -> Unit,
    onEnterApp: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Everything Ready",
            color = Color.White,
            fontSize = 26.sp
        )

        Spacer(Modifier.height(32.dp))

        Button(onClick = onEnterSettings) {
            Text("Customize applications")
        }

        Spacer(Modifier.height(16.dp))


        TextButton(onClick = onEnterApp) {
            Text(
                text = "Don't customize and start using directly Dragon Launcher",
                color = MaterialTheme.colorScheme.onBackground.copy(0.5f),
                textDecoration = TextDecoration.Underline
            )
        }
    }
}

package org.elnix.dragonlauncher.ui.welcome

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import org.elnix.dragonlauncher.R

@Composable
fun WelcomePageIntro() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(R.drawable.ic_app_logo),
            contentDescription = "App Logo",
            modifier = Modifier.size(260.dp)
        )

        Spacer(Modifier.height(32.dp))

        Text(
            "Welcome to Dragon Launcher",
            color = Color.White,
            fontSize = 26.sp
        )

        Spacer(Modifier.height(12.dp))

        Text(
            "Fast. Minimal. Powerful gestures.",
            color = Color(0xFFBBBBBB),
            fontSize = 16.sp
        )
    }
}

package org.elnix.dragonlauncher.ui.helpers

import android.R.attr.fontWeight
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Slider
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.elnix.dragonlauncher.data.stores.DrawerSettingsStore
import org.elnix.dragonlauncher.utils.colors.AppObjectsColors
import androidx.compose.ui.platform.LocalContext

@Composable
fun GridSizeSlider() {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    val gridSize by DrawerSettingsStore.getGridSize(ctx).collectAsState(initial = 1)
    var sliderValue by remember { mutableStateOf(gridSize.toFloat()) }

    LaunchedEffect(gridSize) {
        sliderValue = gridSize.toFloat()
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Grid Size: ${sliderValue.toInt()}",
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )

        Slider(
            value = sliderValue,
            onValueChange = { sliderValue = it },
            onValueChangeFinished = {
                scope.launch { DrawerSettingsStore.setGridSize(ctx, sliderValue.toInt()) }
            },
            valueRange = 1f..10f,
            steps = 8,
            colors = AppObjectsColors.sliderColors()
        )
    }
}

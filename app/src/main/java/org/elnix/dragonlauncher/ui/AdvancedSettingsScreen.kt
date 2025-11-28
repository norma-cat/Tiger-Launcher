package org.elnix.dragonlauncher.ui


import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.elnix.dragonlauncher.data.datastore.SettingsStore
import org.elnix.dragonlauncher.ui.helpers.SwitchRow


@Composable
fun AdvancedSettingsScreen(
    onBack: (() -> Unit)
) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    val rgbLoading by SettingsStore.getRGBLoading(ctx)
        .collectAsState(initial = true)

    val debugInfos by SettingsStore.getDebugInfos(ctx)
        .collectAsState(initial = true)


    val angleLineColor by SettingsStore.getAngleLineColor(ctx)
        .collectAsState(initial = null)

    BackHandler { onBack() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(WindowInsets.systemBars.asPaddingValues())
            .imePadding()
            .padding(15.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ){
        SwitchRow(
            debugInfos,
            "Debug Infos",
        ) { scope.launch { SettingsStore.setDebugInfos(ctx, it) } }


        SwitchRow(
            rgbLoading,
            "RGB loading settings",
        ) { scope.launch { SettingsStore.setRGBLoading(ctx, it) } }


        OutlinedTextField(
            value = angleLineColor?.toArgb()?.toString() ?: "",
            onValueChange = { newText: String ->
                scope.launch {
                    val intValue = newText.toIntOrNull()
                    SettingsStore.setAngleLineColor(ctx, intValue?.let { Color(it) })
                }
            },
            label = { Text("Angle line color") },
            modifier = Modifier
        )
    }
}

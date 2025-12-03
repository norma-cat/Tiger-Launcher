package org.elnix.dragonlauncher.ui.settings.backup

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.elnix.dragonlauncher.data.DataStoreName
import org.elnix.dragonlauncher.ui.SETTINGS.COLORS
import org.elnix.dragonlauncher.utils.colors.AppObjectsColors

@Composable
fun ExportSettingsDialog(
    onDismiss: () -> Unit,
    onConfirm: (selectedStores: List<DataStoreName>) -> Unit
) {
    val allStores = DataStoreName.entries

    val selected = remember(allStores) {
        mutableStateMapOf<DataStoreName, Boolean>().apply {
            allStores.forEach { put(it, true) }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(allStores.filter { selected[it] == true })
                },
                colors = AppObjectsColors.buttonColors()
            ) {
                Text("Export")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = AppObjectsColors.cancelButtonColors()
            ) { Text("Cancel") }
        },
        title = { Text("Select settings to export") },
        text = {
            LazyColumn {
                items(allStores.size) { index ->
                    val store = allStores[index]
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .toggleable(
                                value = selected[store] ?: true,
                                onValueChange = { selected[store] = it }
                            ),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(store.backupKey.replaceFirstChar { it.uppercase() })
                        Checkbox(
                            checked = selected[store] ?: true,
                            onCheckedChange = null
                        )
                    }
                }
            }
        }
    )
}

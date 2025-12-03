package org.elnix.dragonlauncher.ui.settings.backup

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.elnix.dragonlauncher.data.DataStoreName
import org.elnix.dragonlauncher.utils.colors.AppObjectsColors
import org.json.JSONObject
import java.util.Locale

@Composable
fun ImportSettingsDialog(
    backupJson: JSONObject,
    onDismiss: () -> Unit,
    onConfirm: (selectedStores: List<DataStoreName>) -> Unit
) {
    val allStores = DataStoreName.entries

    // Filter stores that exist in backup JSON
    val availableStores = allStores.filter {
        backupJson.has(it.backupKey)
    }

    val selected = remember(availableStores) {
        mutableStateMapOf<DataStoreName, Boolean>().apply {
            availableStores.forEach { put(it, true) }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(availableStores.filter { selected[it] == true })
                },
                colors = AppObjectsColors.buttonColors()
            ) {
                Text("Import")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = AppObjectsColors.cancelButtonColors()
            ) { Text("Cancel") }
        },
        title = { Text("Select settings to import") },
        text = {
            LazyColumn {
                items(availableStores) { store ->
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
                        Text(
                            store.backupKey.replaceFirstChar {
                                if (it.isLowerCase()) it.titlecase(Locale.ROOT)
                                else it.toString()
                            }
                        )
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

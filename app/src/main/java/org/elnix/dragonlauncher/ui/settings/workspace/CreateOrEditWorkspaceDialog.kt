package org.elnix.dragonlauncher.ui.settings.workspace

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.elnix.dragonlauncher.R
import org.elnix.dragonlauncher.ui.drawer.WorkspaceType
import org.elnix.dragonlauncher.ui.helpers.ActionSelectorRow
import org.elnix.dragonlauncher.utils.colors.AppObjectsColors

@Composable
fun CreateOrEditWorkspaceDialog(
    visible: Boolean,
    title: String,
    name: String,
    onNameChange: (String) -> Unit,
    onConfirm: (WorkspaceType) -> Unit,
    onDismiss: () -> Unit
) {
    if (!visible) return

    var selectedType by remember { mutableStateOf(WorkspaceType.CUSTOM) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = { onConfirm(selectedType) },
                colors = AppObjectsColors.buttonColors()
            ) {
                Text(
                    text = stringResource(android.R.string.ok),
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = { onDismiss(); },
                colors = AppObjectsColors.cancelButtonColors()
            ) {
                Text(
                    stringResource(android.R.string.cancel),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        },
        title = { Text(title) },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ){
                TextField(
                    value = name,
                    onValueChange = onNameChange,
                    singleLine = true,
                    placeholder = {
                        Text(stringResource(R.string.workspace_name))
                    },
                    colors = AppObjectsColors.outlinedTextFieldColors(backgroundColor = MaterialTheme.colorScheme.surface, removeBorder = true)
                )

                ActionSelectorRow(
                    options = WorkspaceType.entries,
                    selected = selectedType,
                    switchEnabled = false,
                    toggled = true,
                    label = stringResource(R.string.workspace_type)
                ) {
                    selectedType = it
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp,
        shape = RoundedCornerShape(20.dp)
    )
}

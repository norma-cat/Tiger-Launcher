package org.elnix.dragonlauncher.ui.settings.workspace

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import org.elnix.dragonlauncher.ui.drawer.Workspace
import org.elnix.dragonlauncher.ui.drawer.WorkspaceType
import org.elnix.dragonlauncher.utils.colors.AppObjectsColors
import org.elnix.dragonlauncher.utils.workspace.WorkspaceAction

@Composable
fun WorkspaceRow(
    workspace: Workspace,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onCheck: (Boolean) -> Unit,
    onAction: (WorkspaceAction) -> Unit
) {
    val enabled = workspace.enabled
    val editable = workspace.type == WorkspaceType.CUSTOM

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
            .background(MaterialTheme.colorScheme.primary)
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = enabled,
            enabled = workspace.type == WorkspaceType.CUSTOM,
            onCheckedChange = { onCheck(it) },
            colors = AppObjectsColors.checkboxColors()
        )

        Text(
            text = workspace.name,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )

        if (editable) {
            Row(
                modifier = Modifier.padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf(
                    WorkspaceAction.Rename,
                    WorkspaceAction.Delete
                ).forEach { action ->
                    IconButton(onClick = { onAction(action) }) {
                        Icon(action.icon, action.label)
                    }
                }
            }
        }
    }
}

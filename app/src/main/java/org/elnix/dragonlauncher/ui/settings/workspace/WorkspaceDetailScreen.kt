package org.elnix.dragonlauncher.ui.settings.workspace

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import org.elnix.dragonlauncher.ui.helpers.AppGrid
import org.elnix.dragonlauncher.ui.helpers.settings.SettingsLazyHeader
import org.elnix.dragonlauncher.utils.AppDrawerViewModel
import org.elnix.dragonlauncher.utils.workspace.WorkspaceViewModel

@Composable
fun WorkspaceDetailScreen(
    showLabels: Boolean,
    showIcons: Boolean,
    gridSize: Int,
    workspaceId: String,
    appsViewModel: AppDrawerViewModel,
    workspaceViewModel: WorkspaceViewModel,
    onBack: () -> Unit
) {
    val workspaceState by workspaceViewModel.state.collectAsState()
    val workspace = workspaceState.workspaces.first { it.id == workspaceId }

    val apps by appsViewModel
        .appsForWorkspace(workspace, workspaceState.appOverrides)
        .collectAsState(initial = emptyList())

    val icons by appsViewModel.icons.collectAsState()

    SettingsLazyHeader(
        title = workspace.name,
        onBack = onBack,
        helpText = "",
        onReset = {},
        content = {
            AppGrid(
                apps = apps,
                icons = icons,
                gridSize = gridSize,
                txtColor = Color.White,
                showIcons = showIcons,
                showLabels = showLabels,
                onClick = { }
            )
        }
    )
}

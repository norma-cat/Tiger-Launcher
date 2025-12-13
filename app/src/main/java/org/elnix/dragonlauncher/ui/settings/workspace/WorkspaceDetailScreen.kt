@file:Suppress("AssignedValueIsNeverRead")

package org.elnix.dragonlauncher.ui.settings.workspace

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.elnix.dragonlauncher.R
import org.elnix.dragonlauncher.ui.drawer.AppLongPressDialog
import org.elnix.dragonlauncher.ui.drawer.AppModel
import org.elnix.dragonlauncher.ui.drawer.WorkspaceType
import org.elnix.dragonlauncher.ui.helpers.AppGrid
import org.elnix.dragonlauncher.ui.helpers.AppPickerDialog
import org.elnix.dragonlauncher.ui.helpers.settings.SettingsLazyHeader
import org.elnix.dragonlauncher.utils.AppDrawerViewModel
import org.elnix.dragonlauncher.utils.showToast
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
    val scope = rememberCoroutineScope()
    val ctx = LocalContext.current

    val workspaceState by workspaceViewModel.state.collectAsState()
    val workspace = workspaceState.workspaces.first { it.id == workspaceId }

    val apps by appsViewModel
        .appsForWorkspace(workspace, workspaceState.appOverrides)
        .collectAsState(initial = emptyList())

    val icons by appsViewModel.icons.collectAsState()

    var showAppPicker by remember { mutableStateOf(false) }
    var showDetailScreen by remember { mutableStateOf<AppModel?>(null) }


    val isCustom = workspace.type == WorkspaceType.CUSTOM


    Box(Modifier.fillMaxSize()) {
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
                    onLongClick = if (isCustom) { { showDetailScreen = it } } else null,
                    onClick = if (isCustom) { { app -> showDetailScreen = app } } else { _ -> }
                )
            }
        )

        FloatingActionButton(
            onClick = {
                if (isCustom) { showAppPicker = true }
                else ctx.showToast(R.string.system_workspace_cant_edit)
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = if (isCustom) MaterialTheme.colorScheme.primary else Color.DarkGray
        ) {
            Icon(Icons.Default.Add, null)
        }
    }

    if (showAppPicker) {
        AppPickerDialog(
            appsViewModel = appsViewModel,
            workspaceViewModel = workspaceViewModel,
            gridSize = gridSize,
            showIcons = showIcons,
            showLabels = showLabels,
            onDismiss = { showAppPicker = false }
        ) { app ->
            scope.launch {
                workspaceViewModel.addAppToWorkspace(workspaceId, app.packageName)
            }
        }
    }

    if (showDetailScreen != null) {
        val app = showDetailScreen!!
        AppLongPressDialog(
            app = app,
            showWorkspaceEntries = true,
            showNormalEntries = false,
            onRemoveFromWorkspace = {
                scope.launch {
                    workspaceViewModel.removeAppFromWorkspace(
                        workspaceId,
                        app.packageName
                    )
                }
            }
        ) { showDetailScreen = null }
    }
}

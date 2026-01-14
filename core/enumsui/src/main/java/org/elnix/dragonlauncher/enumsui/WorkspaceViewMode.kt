package org.elnix.dragonlauncher.enumsui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

enum class WorkspaceViewMode {
    DEFAULTS,
    ADDED,
    REMOVED
}

@Composable
fun workspaceViewMode(mode: WorkspaceViewMode): String {
    return when(mode) {
        WorkspaceViewMode.DEFAULTS -> stringResource(R.string.workspace_defaults)
        WorkspaceViewMode.ADDED -> stringResource(R.string.workspace_added)
        WorkspaceViewMode.REMOVED -> stringResource(R.string.workspace_removed)
    }
}

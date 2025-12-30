package org.elnix.dragonlauncher.ui.components.dialogs

import android.content.pm.ShortcutInfo
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.elnix.dragonlauncher.R
import org.elnix.dragonlauncher.data.SwipeActionSerializable
import org.elnix.dragonlauncher.data.stores.DrawerSettingsStore
import org.elnix.dragonlauncher.ui.actionTint
import org.elnix.dragonlauncher.ui.drawer.AppModel
import org.elnix.dragonlauncher.ui.theme.LocalExtraColors
import org.elnix.dragonlauncher.utils.PackageManagerCompat
import org.elnix.dragonlauncher.utils.actions.actionColor
import org.elnix.dragonlauncher.utils.actions.actionIconBitmap
import org.elnix.dragonlauncher.utils.actions.actionLabel
import org.elnix.dragonlauncher.utils.actions.loadDrawableResAsBitmap
import org.elnix.dragonlauncher.utils.models.AppsViewModel
import org.elnix.dragonlauncher.utils.models.WorkspaceViewModel

@Suppress("AssignedValueIsNeverRead")
@Composable
fun AddPointDialog(
    appsViewModel: AppsViewModel,
    workspaceViewModel: WorkspaceViewModel,
    onDismiss: () -> Unit,
    onActionSelected: (SwipeActionSerializable) -> Unit
) {
    val ctx = LocalContext.current

    val pm = ctx.packageManager
    val packageManagerCompat = PackageManagerCompat(pm, ctx)

    var showAppPicker by remember { mutableStateOf(false) }
    var showUrlInput by remember { mutableStateOf(false) }
    var showFilePicker by remember { mutableStateOf(false) }

    val actions = listOf(
        SwipeActionSerializable.OpenCircleNest(0),
        SwipeActionSerializable.GoParentNest,
        SwipeActionSerializable.LaunchApp(""),
        SwipeActionSerializable.OpenUrl(""),
        SwipeActionSerializable.OpenFile(""),
        SwipeActionSerializable.NotificationShade,
        SwipeActionSerializable.ControlPanel,
        SwipeActionSerializable.OpenAppDrawer,
        SwipeActionSerializable.Lock,
        SwipeActionSerializable.ReloadApps,
        SwipeActionSerializable.OpenRecentApps,
        SwipeActionSerializable.OpenDragonLauncherSettings
    )

    val icons by appsViewModel.icons.collectAsState()

    val gridSize by DrawerSettingsStore.getGridSize(ctx)
        .collectAsState(initial = 1)
    val showIcons by DrawerSettingsStore.getShowAppIconsInDrawer(ctx)
        .collectAsState(initial = true)
    val showLabels by DrawerSettingsStore.getShowAppLabelsInDrawer(ctx)
        .collectAsState(initial = true)


    var selectedApp by remember { mutableStateOf<AppModel?>(null) }
    var shortcutDialogVisible by remember { mutableStateOf(false) }
    var shortcuts by remember { mutableStateOf<List<ShortcutInfo>>(emptyList()) }


    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        title = { Text(stringResource(R.string.choose_action)) },
        text = {
            LazyVerticalGrid(
                modifier = Modifier
                    .height(320.dp)
                    .clip(RoundedCornerShape(12.dp)),
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(5.dp),
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                // Loop through all actions
                items(actions) { action ->
                    when (action) {

                        // Open App → requires AppPicker
                        is SwipeActionSerializable.LaunchApp -> {
                            AddPointColumn(
                                action = action,
                                icons = icons,
                                onSelected = { showAppPicker = true }
                            )
                            Spacer(Modifier.height(8.dp))
                        }

                        // Open URL → requires URL dialog
                        is SwipeActionSerializable.OpenUrl -> {
                            AddPointColumn(
                                action = action,
                                icons = icons,
                                onSelected = { showUrlInput = true }
                            )
                            Spacer(Modifier.height(8.dp))
                        }

                        // Open File picker to choose a file
                        is SwipeActionSerializable.OpenFile -> {
                            AddPointColumn(
                                action = action,
                                icons = icons,
                                onSelected = { showFilePicker = true }
                            )
                            Spacer(Modifier.height(8.dp))
                        }

//                        is SwipeActionSerializable.OpenCircleNest -> {
//                            AddPointColumn(
//                                action = action,
//                                icons = icons,
//                                onSelected = {
//                                    onActionSelected(action)
//                                    TODO("CREATE NEW NEST")
//                                }
//                            )
//                            Spacer(Modifier.height(8.dp))
//                        }
                        // Direct actions
                        else -> {
                            AddPointColumn(
                                action = action,
                                icons = icons,
                                onSelected = { onActionSelected(action) }
                            )
                            Spacer(Modifier.height(8.dp))
                        }
                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.surface
    )

    if (showAppPicker) {
        AppPickerDialog(
            appsViewModel = appsViewModel,
            workspaceViewModel = workspaceViewModel,
            gridSize = gridSize,
            showIcons = showIcons,
            showLabels = showLabels,
            onDismiss = { showAppPicker = false },
            onAppSelected = { app ->


                val list = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    packageManagerCompat.queryAppShortcuts(app.packageName)
                } else {
                    emptyList()
                }

                if (list.isNotEmpty()) {
                    selectedApp = app
                    shortcuts = list
                    shortcutDialogVisible = true
                } else {
                    onActionSelected(SwipeActionSerializable.LaunchApp(app.packageName))
                }
            }
        )
    }

    if (showUrlInput) {
        UrlInputDialog(
            onDismiss = { showUrlInput = false },
            onUrlSelected = {
                onActionSelected(it)
                showUrlInput = false
            }
        )
    }

    if (showFilePicker) {
        FilePickerDialog(
            onDismiss = { showFilePicker = false },
            onFileSelected = {
                onActionSelected(it)
                showFilePicker = false
            }
        )
    }

    if (shortcutDialogVisible && selectedApp != null) {
        AppShortcutPickerDialog(
            app = selectedApp!!,
            icons = icons,
            shortcuts = shortcuts,
            onDismiss = { shortcutDialogVisible = false },
            onShortcutSelected = {pkg, id ->
                onActionSelected(SwipeActionSerializable.LaunchShortcut(pkg, id))
                shortcutDialogVisible = false
            },
            onOpenApp = {
                onActionSelected(SwipeActionSerializable.LaunchApp(selectedApp!!.packageName))
                onDismiss()
            }
        )
    }
}


@Composable
fun AddPointColumn(
    action: SwipeActionSerializable,
    icons: Map<String, ImageBitmap>,
    onSelected: () -> Unit
) {
    val ctx = LocalContext.current
    val extraColors = LocalExtraColors.current


    val icon = when(action) {
        is SwipeActionSerializable.LaunchApp -> loadDrawableResAsBitmap(ctx, R.drawable.ic_app_grid)
        else -> actionIconBitmap(
            icons,
            action,
            ctx,
            tintColor = actionColor(action, extraColors)
        )
    }

    val name = when(action) {
        is SwipeActionSerializable.LaunchApp -> "Open App"
        is SwipeActionSerializable.OpenUrl -> "Open Url"
        is SwipeActionSerializable.OpenFile -> "Open File"
        else -> actionLabel(action)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(actionColor(action, extraColors).copy(0.5f))
            .clickable { onSelected() }
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = name,
            color = Color.White,
            textAlign = TextAlign.Center
        )
        Icon(
            bitmap = icon,
            contentDescription = action.toString(),
            tint = actionTint(action, extraColors),
            modifier = Modifier.size(30.dp)
        )
    }
}

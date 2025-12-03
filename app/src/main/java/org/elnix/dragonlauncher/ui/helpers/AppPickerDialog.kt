package org.elnix.dragonlauncher.ui.helpers

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.elnix.dragonlauncher.data.SwipeActionSerializable
import org.elnix.dragonlauncher.data.stores.DrawerSettingsStore
import org.elnix.dragonlauncher.ui.drawer.AppItem
import org.elnix.dragonlauncher.ui.drawer.AppModel
import org.elnix.dragonlauncher.utils.AppDrawerViewModel
import org.elnix.dragonlauncher.utils.actions.appIcon
import org.elnix.dragonlauncher.utils.colors.AppObjectsColors

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppPickerDialog(
    viewModel: AppDrawerViewModel,
    gridSize: Int,
    initialPage: Int,
    onDismiss: () -> Unit,
    onAppSelected: (SwipeActionSerializable.LaunchApp) -> Unit
) {
    val userApps by viewModel.userApps.collectAsState()
    val systemApps by viewModel.systemApps.collectAsState()
    val allApps by viewModel.allApps.collectAsState()
    val icons by viewModel.icons.collectAsState()

    val pages = listOf("User", "System", "All")
    val pagerState = rememberPagerState(
        initialPage = initialPage,
        pageCount = { pages.size }
    )

    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(pagerState.currentPage) {
        scope.launch {
            DrawerSettingsStore.setInitialPage(ctx, pagerState.currentPage)
        }
    }


    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Select App")

                Spacer(Modifier.height(6.dp))

                // --- PAGE INDICATOR ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    pages.forEachIndexed { index, label ->
                        val selected = pagerState.currentPage == index


                        TextButton(
                            onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
                            shape = RoundedCornerShape(12.dp),
                            colors = AppObjectsColors.buttonColors(if (!selected) MaterialTheme.colorScheme.surface else null),
                            modifier = Modifier.padding(5.dp)
                        ) {
                            Text(
                                text = label,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        },
        text = {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.height(350.dp)
            ) { pageIndex ->

                val list = when (pageIndex) {
                    0 -> userApps
                    1 -> systemApps
                    else -> allApps
                }

                AppGrid(
                    apps = list,
                    icons = icons,
                    gridSize = gridSize,
                    txtColor = MaterialTheme.colorScheme.onSurface,
                    showIcons = true,
                    showLabels = true
                ) {
                    onAppSelected(SwipeActionSerializable.LaunchApp(it.packageName))
                    onDismiss()
                }
            }
        },
        confirmButton = {},
        containerColor = MaterialTheme.colorScheme.surface
    )
}


@Composable
fun AppGrid(
    apps: List<AppModel>,
    icons: Map<String, ImageBitmap>,
    gridSize: Int,
    txtColor: Color,
    showIcons: Boolean,
    showLabels: Boolean,
    onLongClick: ((AppModel) -> Unit)? = null,
    onClick: (AppModel) -> Unit
) {
    if (gridSize == 1) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .pointerInput(Unit) {
                    detectVerticalDragGestures { _, _ -> }
                }
        ) {
            items(apps) { app ->
                AppItem(
                    app = app,
                    showIcons = showIcons,
                    showLabels = showLabels,
                    icons = icons,
                    onClick = { onClick(app) },
                    onLongClick = if (onLongClick != null) { { onLongClick(app) } } else null
                )
            }
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(gridSize),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(apps.size) { index ->
                val app = apps[index]

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onClick(app) }
                        .padding(8.dp)
                ) {
                    if (showIcons) {
                        Image(
                            painter = appIcon(app.packageName, icons),
                            contentDescription = app.name,
                            modifier = Modifier.size(48.dp)
                        )
                    }

                    if (showLabels) {
                        Spacer(Modifier.height(6.dp))

                        Text(
                            text = app.name,
                            color = txtColor,
                            style = MaterialTheme.typography.labelSmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

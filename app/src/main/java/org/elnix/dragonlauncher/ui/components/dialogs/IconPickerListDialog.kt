package org.elnix.dragonlauncher.ui.components.dialogs

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.elnix.dragonlauncher.R
import org.elnix.dragonlauncher.utils.colors.AppObjectsColors
import org.elnix.dragonlauncher.utils.colors.adjustBrightness

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun IconPickerListDialog(
    icons: Map<String, ImageBitmap>,
    showNames: Boolean = true,
    onDismiss: () -> Unit,
    onIconSelected: (iconName: String, icon: ImageBitmap) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var isSearchEnabled by remember { mutableStateOf(false) }

    val filteredIcons = remember(searchQuery, icons) {
        if (searchQuery.isBlank()) icons
        else icons.filterKeys {
            it.contains(searchQuery, ignoreCase = true)
        }
    }

    CustomAlertDialog(
        imePadding = false,
        scroll = false,
        alignment = Alignment.Center,
        modifier = Modifier.padding(16.dp),
        onDismissRequest = onDismiss,
        title = {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .height(75.dp)
                ) {
                    if (!isSearchEnabled) {
                        Text(
                            text = "Select Icon",
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.titleMedium
                        )

                        IconButton(onClick = { isSearchEnabled = true }) {
                            Icon(Icons.Default.Search, contentDescription = null)
                        }
                    } else {
                        TextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            singleLine = true,
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = null,
                                    modifier = Modifier.clickable {
                                        searchQuery = ""
                                        isSearchEnabled = false
                                    }
                                )
                            },
                            placeholder = { Text("Search icons") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(CircleShape),
                            colors = AppObjectsColors.outlinedTextFieldColors(
                                removeBorder = true,
                                backgroundColor = MaterialTheme.colorScheme.surface.adjustBrightness(0.8f)
                            )
                        )
                    }
                }
            }
        },
        text = {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(72.dp),
                modifier = Modifier
                    .height(500.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (filteredIcons.isNotEmpty()) {
                    items(filteredIcons.entries.toList()) { (name, bitmap) ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    onIconSelected(name, bitmap)
                                    onDismiss()
                                }
                                .padding(6.dp)
                        ) {
                            Icon(
                                painter = BitmapPainter(bitmap),
                                contentDescription = name,
                                modifier = Modifier.size(40.dp),
                                tint = MaterialTheme.colorScheme.onSurface
                            )

                            if (showNames && name.isNotBlank()) {
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = name,
                                    fontSize = 11.sp,
                                    maxLines = 1,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                } else if (icons.isNotEmpty()) {
                    item {
                        Text(stringResource(R.string.no_search_match))
                    }
                } else {
                    item {
                        CircularProgressIndicator()
                    }
                }
            }
        },
        confirmButton = {},
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(12.dp)
    )
}

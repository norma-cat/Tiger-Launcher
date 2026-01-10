@file:Suppress("AssignedValueIsNeverRead")

package org.elnix.dragonlauncher.ui.components.dialogs

import android.graphics.Bitmap
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.unit.dp
import org.elnix.dragonlauncher.ui.helpers.iconPackListContent
import org.elnix.dragonlauncher.utils.models.AppsViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun IconPackPickerDialog(
    appsViewModel: AppsViewModel,
    onDismiss: () -> Unit,
    onIconPicked: (Bitmap) -> Unit
) {

    var showIconPickerDialog by remember { mutableStateOf(false) }

    val icons by appsViewModel.icons.collectAsState()
    val packIcons by appsViewModel.packIcons.collectAsState()

    val packs = appsViewModel.findIconPacks()

    AlertDialog(
        onDismissRequest = onDismiss,
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                iconPackListContent(
                    packs = packs,
                    icons = icons,
                    selectedPackPackage = null,
                    showClearOption = false,
                    onPackClick = { pack ->
                        appsViewModel.loadAllIconsFromPack(pack)
                        showIconPickerDialog = true
                    },
                    onClearClick = {}
                )

            }
        },
        confirmButton = {},
        containerColor = MaterialTheme.colorScheme.surface
    )

    if (showIconPickerDialog) {
        IconPickerListDialog(
            icons = packIcons,
            onDismiss = { showIconPickerDialog = false },
            onIconSelected = {
                _, iconBitmap ->
                onIconPicked(iconBitmap.asAndroidBitmap())
            }
        )
    }
}

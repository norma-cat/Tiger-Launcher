@file:Suppress("AssignedValueIsNeverRead", "DEPRECATION")

package org.elnix.dragonlauncher.ui.components.dialogs

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.center
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import kotlinx.coroutines.launch
import org.elnix.dragonlauncher.R
import org.elnix.dragonlauncher.data.SwipePointSerializable
import org.elnix.dragonlauncher.data.helpers.CustomIconSerializable
import org.elnix.dragonlauncher.data.helpers.IconType
import org.elnix.dragonlauncher.ui.components.ValidateCancelButtons
import org.elnix.dragonlauncher.ui.helpers.actionsInCircle
import org.elnix.dragonlauncher.ui.theme.LocalExtraColors
import org.elnix.dragonlauncher.utils.ImageUtils.bitmapToBase64
import org.elnix.dragonlauncher.utils.ImageUtils.uriToBase64
import org.elnix.dragonlauncher.utils.actions.actionColor
import org.elnix.dragonlauncher.utils.colors.AppObjectsColors
import org.elnix.dragonlauncher.utils.colors.adjustBrightness
import org.elnix.dragonlauncher.utils.models.AppsViewModel

@Composable
fun IconEditorDialog(
    point: SwipePointSerializable,
    appsViewModel: AppsViewModel,
    onDismiss: () -> Unit,
    onPicked: (CustomIconSerializable?) -> Unit
) {
    val ctx = LocalContext.current
    val extraColors = LocalExtraColors.current
    val scope = rememberCoroutineScope()

    val circleColor = LocalExtraColors.current.circle

    val pointIcons by appsViewModel.pointIcons.collectAsState()

    val icon = point.customIcon
    var selectedIcon by remember { mutableStateOf(icon) }
    var textValue by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        if (selectedIcon?.type == IconType.TEXT) {
            textValue = selectedIcon?.source ?: ""
        }
    }

    val source = selectedIcon?.source

    var showIconPackPicker by remember { mutableStateOf(false) }

    val cropLauncher = rememberLauncherForActivityResult(
        CropImageContract()
    ) { result ->
        val uri = result.uriContent ?: return@rememberLauncherForActivityResult

        scope.launch {
            val base64 = uriToBase64(ctx, uri)
            selectedIcon = (selectedIcon ?: CustomIconSerializable()).copy(
                type = IconType.BITMAP,
                source = base64
            )
        }
    }

    val imagePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri ?: return@rememberLauncherForActivityResult

        cropLauncher.launch(
            CropImageContractOptions(
                uri,
                cropImageOptions = CropImageOptions(
                    cropShape = CropImageView.CropShape.RECTANGLE,
                    fixAspectRatio = true,
                    aspectRatioX = 1,
                    aspectRatioY = 1,
                    guidelines = CropImageView.Guidelines.ON
                )
            )
        )
    }

    fun reloadIcon() {
        appsViewModel.ensurePointIcon(
            ctx = ctx,
            point = point,
            tint = actionColor(point.action, extraColors),
        )
    }

    LaunchedEffect(selectedIcon.hashCode()) { reloadIcon() }

    CustomAlertDialog(
        alignment = Alignment.Center,
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.icon_picker),
                    style = MaterialTheme.typography.titleLarge
                )

                Canvas(
                    modifier = Modifier
                        .width(50.dp)
                ) {
                    val center = size.center
                    val drawScope = this

                    // Left action
                    actionsInCircle(
                        selected = false,
                        drawScope = drawScope,
                        point = point,
                        nests = emptyList(),
                        px = center.x,
                        py = center.y,
                        ctx = ctx,
                        circleColor = circleColor,
                        colorAction = actionColor(point.action, extraColors),
                        preventBgErasing = true,
                        pointIcons = pointIcons
                    )
                }

                IconButton(
                    onClick = {
                        selectedIcon = null
                        textValue = ""
                    },
                    colors = AppObjectsColors.iconButtonColors()
                ) {
                    Icon(
                        imageVector = Icons.Default.Restore,
                        contentDescription  = stringResource(R.string.reset)
                    )
                }
            }
        },
        confirmButton = {
            ValidateCancelButtons(
                onCancel = onDismiss,
            ) { onPicked(selectedIcon) }
        },
        text = {
            LazyVerticalGrid(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(520.dp),
                columns = GridCells.Fixed(2)
            ) {

                item {
                    SelectableCard(
                        selected = selectedIcon?.type == IconType.BITMAP && source != null,
                        onClick = {
                            imagePicker.launch(arrayOf("image/*"))
                        }
                    ) {
                        Icon(Icons.Default.Image, null)
                        Spacer(Modifier.width(12.dp))
                        Text(stringResource(R.string.pick_image))
                    }
                }

                item {
                    SelectableCard(
                        selected = selectedIcon?.type == IconType.TEXT && source != null,
                        onClick = {}
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surface.adjustBrightness(0.7f))
                                .padding(12.dp)
                        ) {
                            Text("Text / Emoji", fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(8.dp))
                            TextField(
                                value = textValue,
                                onValueChange = {
                                    textValue = it
                                    selectedIcon =
                                        if (it.isNotBlank()) {
                                            (selectedIcon ?: CustomIconSerializable()).copy(
                                                type = IconType.TEXT,
                                                source = it
                                            )
                                        } else {
                                            null
                                        }
                                },
                                placeholder = { Text("😀  A  ★") },
                                singleLine = true,
                                colors = AppObjectsColors.outlinedTextFieldColors(
                                    removeBorder = true,
                                    backgroundColor = MaterialTheme.colorScheme.surface
                                )
                            )
                        }
                    }
                }

                item {
                    SelectableCard(
                        selected = selectedIcon?.type == IconType.ICON_PACK && source != null,
                        onClick = { showIconPackPicker = true }
                    ) {
                        Icon(Icons.Default.Palette, null)
                        Spacer(Modifier.width(12.dp))
                        Text(stringResource(R.string.pick_from_icon_pack))
                    }
                }

                item {
                    SelectableCard(
                        selected = selectedIcon?.type == null || selectedIcon?.source == null,
                        onClick = {
                            selectedIcon = selectedIcon?.copy(
                                type = null,
                                source = null
                            )
                            textValue = ""
                        }
                    ) {
                        Icon(Icons.Default.Restore, null)
                        Spacer(Modifier.width(12.dp))
                        Text(stringResource(R.string.no_custom_icon))
                    }
                }
            }

        },
        containerColor = MaterialTheme.colorScheme.surface
    )

    if (showIconPackPicker) {
         IconPackPickerDialog(
             appsViewModel = appsViewModel,
             onDismiss = { showIconPackPicker = false },
             onIconPicked = { iconBitmap ->
                 scope.launch {
                     val base64 = bitmapToBase64(iconBitmap)
                     selectedIcon = selectedIcon?.copy(
                         type = IconType.ICON_PACK,
                         source = base64
                     )
                     showIconPackPicker = false
                 }
             }
         )
    }
}

@Composable
private fun SelectableCard(
    selected: Boolean,
    onClick: () -> Unit,
    content: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(6.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface.adjustBrightness(0.7f))
            .clickable { onClick() }
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            content = content,
            modifier = Modifier.weight(1f)
        )

        if (selected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

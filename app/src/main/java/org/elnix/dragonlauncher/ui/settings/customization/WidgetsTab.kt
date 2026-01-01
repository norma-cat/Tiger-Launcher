package org.elnix.dragonlauncher.ui.settings.customization

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoveDown
import androidx.compose.material.icons.filled.MoveUp
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.elnix.dragonlauncher.MainActivity
import org.elnix.dragonlauncher.R
import org.elnix.dragonlauncher.data.helpers.WidgetInfo
import org.elnix.dragonlauncher.ui.components.WidgetHostView
import org.elnix.dragonlauncher.ui.helpers.CircleIconButton
import org.elnix.dragonlauncher.ui.helpers.settings.SettingsLazyHeader
import org.elnix.dragonlauncher.utils.models.WidgetsViewModel

@Composable
fun WidgetsTab(
    widgetsViewModel: WidgetsViewModel,
    onBack: () -> Unit,
    onLaunchSystemWidgetPicker: () -> Unit
) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    val widgets by widgetsViewModel.widgets.collectAsState()

    var selected by remember { mutableStateOf<WidgetInfo?>(null) }

    val isSelected = selected != null


    fun removeWidget(widget: WidgetInfo) {
        widgetsViewModel.removeWidget(widget.id) {
            (ctx as MainActivity).deleteWidget(it)
        }

        if (selected == widget) selected = null
    }

    SettingsLazyHeader(
        title = "${stringResource(R.string.widgets)} (${widgets.size})",
        onBack = onBack,
        helpText = stringResource(R.string.widgets_tab_help),
        onReset = {
            scope.launch {
                widgetsViewModel.resetAllWidgets()
            }
        },
        content = {
            Box(
                Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures {
                            selected = null
                        }
                    }
            )
        }
    )

    Box(Modifier.fillMaxSize()) {


        /* ---------------- Widget canvas ---------------- */

        widgets.forEach { widget ->
            key(widget.id) {
                DraggableWidget(
                    widgetsViewModel = widgetsViewModel,
                    widget = widget,
                    selected = widget.id == selected?.id,
                    onSelect = { selected = widget },
                    onMove = { dx, dy ->
                        widgetsViewModel.offsetWidget(widget.id, dx, dy)
                    },
                    onResize = { corner, dx, dy ->
                        widgetsViewModel.resizeWidget(widget.id, corner, dx, dy)
                    },
                    onRemove = { removeWidget(it) }
                )
            }
        }

        /* ---------------- Bottom controls ---------------- */


        Row(
            Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Delete selected widget
            CircleIconButton(
                icon = Icons.Default.Delete,
                contentDescription = stringResource(R.string.delete_widget),
                color = MaterialTheme.colorScheme.error,
                enabled = isSelected,
                padding = 16.dp
            ) {
                selected?.let { removeWidget(it) }
            }

            // Launch system widget picker
            CircleIconButton(
                icon = Icons.Default.Add,
                contentDescription = stringResource(R.string.add_widget),
                color = MaterialTheme.colorScheme.primary,
                padding = 16.dp
            ) {
                onLaunchSystemWidgetPicker()
            }

            // Select previous widget
            CircleIconButton(
                icon = Icons.Default.ArrowUpward,
                contentDescription = stringResource(R.string.select_previous_widget),
                color = MaterialTheme.colorScheme.primary,
                padding = 16.dp
            ) {
                if (widgets.isNotEmpty()) {
                    val idx = widgets.indexOfFirst { it == selected }
                    val next = if (idx <= 0) widgets.last() else widgets[idx - 1]
                    selected = next
                }
            }

            // Select next widget
            CircleIconButton(
                icon = Icons.Default.ArrowDownward,
                contentDescription = stringResource(R.string.select_next_widget),
                color = MaterialTheme.colorScheme.primary,
                padding = 16.dp
            ) {
                if (widgets.isNotEmpty()) {
                    val idx = widgets.indexOfFirst { it == selected }
                    val next = if (idx == -1 || idx == widgets.lastIndex) widgets.first() else widgets[idx + 1]
                    selected = next
                }
            }

            // Move selected widget up
            CircleIconButton(
                icon = Icons.Default.MoveDown,
                contentDescription = stringResource(R.string.move_widget_up),
                color = MaterialTheme.colorScheme.primary,
                enabled = isSelected,
                padding = 16.dp
            ) {
                selected?.let { widgetsViewModel.moveWidgetUp(it.id) }
            }

            // Move selected widget down
            CircleIconButton(
                icon = Icons.Default.MoveUp,
                contentDescription = stringResource(R.string.move_widget_down),
                color = MaterialTheme.colorScheme.primary,
                enabled = isSelected,
                padding = 16.dp
            ) {
                selected?.let { widgetsViewModel.moveWidgetDown(it.id) }
            }
        }
    }
}






/**
 * Handles all widget interactions: drag to move, resize handles, tap/long-press.
 * Resize handles provide visual-only resize feedback by compensating position changes internally.
 *
 * @param widgetsViewModel ViewModel for widget state management
 * @param widget Current widget data
 * @param selected True if this widget is currently selected
 * @param onSelect Callback when widget is tapped/selected
 * @param onMove Callback for position drag deltas (dx, dy in pixels)
 * @param onResize Callback for resize drag (corner, dx, dy in pixels)
 * @param onRemove Callback for long-press removal
 */
@SuppressLint("LocalContextResourcesRead")
@Composable
private fun DraggableWidget(
    widgetsViewModel: WidgetsViewModel,
    widget: WidgetInfo,
    selected: Boolean,
    onSelect: () -> Unit,
    onMove: (Float, Float) -> Unit,
    onResize: (WidgetsViewModel.ResizeCorner, Float, Float) -> Unit,
    onRemove: (WidgetInfo) -> Unit
) {
    val ctx = LocalContext.current
    val dm = ctx.resources.displayMetrics
    val borderColor = if (selected) MaterialTheme.colorScheme.primary else Color.Transparent
    val shape = RoundedCornerShape(12.dp)

    Box(
        modifier = Modifier
            .offset {
                IntOffset(
                    x = (widget.x * dm.widthPixels).toInt(),
                    y = (widget.y * dm.heightPixels).toInt()
                )
            }
            .size(
                width = (widget.spanX * 100f).dp,
                height = (widget.spanY * 100f).dp
            )
            .border(
                width = if (selected) 2.dp else 0.dp,
                color = borderColor,
                shape = shape
            )
    ) {
        // Widget content (touch blocked during editing)
        WidgetHostView(
            widgetInfo = widget,
            blockTouches = true
        )

        // Main interaction overlay (move + tap)
        Box(
            modifier = Modifier
                .matchParentSize()
                .pointerInput(widget.id) {
                    detectTapGestures(
                        onPress = { onSelect() },
                        onLongPress = { onRemove(widget) }
                    )
                }
                .pointerInput(widget.id) {
                    detectDragGestures(
                        onDragStart = { onSelect() },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            onMove(dragAmount.x, dragAmount.y)
                        }
                    )
                }
        )

        // Resize handles - only visible when selected
        if (selected) {
            val dotSize = 12.dp
            val hitboxPadding = 20.dp

            // Top handle
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = -((dotSize.value / 2 + hitboxPadding.value).dp))
                    .size(dotSize + hitboxPadding * 2)
                    .clip(CircleShape)
                    .background(Color.Transparent)
                    .pointerInput(WidgetsViewModel.ResizeCorner.Top) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            onResize(WidgetsViewModel.ResizeCorner.Top, 0f, dragAmount.y)
                        }
                    }
            ) {
                Box(
                    modifier = Modifier
                        .size(dotSize)
                        .align(Alignment.Center)
                        .background(MaterialTheme.colorScheme.primary, CircleShape)
                )
            }

            // Bottom handle
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = ((dotSize.value / 2 + hitboxPadding.value).dp))
                    .size(dotSize + hitboxPadding * 2)
                    .clip(CircleShape)
                    .background(Color.Transparent)
                    .pointerInput(WidgetsViewModel.ResizeCorner.Bottom) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            onResize(WidgetsViewModel.ResizeCorner.Bottom, 0f, dragAmount.y)
                        }
                    }
            ) {
                Box(
                    modifier = Modifier
                        .size(dotSize)
                        .align(Alignment.Center)
                        .background(MaterialTheme.colorScheme.primary, CircleShape)
                )
            }

            // Left handle
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .offset(x = -((dotSize.value / 2 + hitboxPadding.value).dp))
                    .size(dotSize + hitboxPadding * 2)
                    .clip(CircleShape)
                    .background(Color.Transparent)
                    .pointerInput(WidgetsViewModel.ResizeCorner.Left) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            onResize(WidgetsViewModel.ResizeCorner.Left, dragAmount.x, 0f)
                        }
                    }
            ) {
                Box(
                    modifier = Modifier
                        .size(dotSize)
                        .align(Alignment.Center)
                        .background(MaterialTheme.colorScheme.primary, CircleShape)
                )
            }

            // Right handle
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .offset(x = ((dotSize.value / 2 + hitboxPadding.value).dp))
                    .size(dotSize + hitboxPadding * 2)
                    .clip(CircleShape)
                    .background(Color.Transparent)
                    .pointerInput(WidgetsViewModel.ResizeCorner.Right) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            onResize(WidgetsViewModel.ResizeCorner.Right, dragAmount.x, 0f)
                        }
                    }
            ) {
                Box(
                    modifier = Modifier
                        .size(dotSize)
                        .align(Alignment.Center)
                        .background(MaterialTheme.colorScheme.primary, CircleShape)
                )
            }
        }
    }
}

package org.elnix.dragonlauncher.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.elnix.dragonlauncher.data.SwipePointSerializable
import org.elnix.dragonlauncher.ui.theme.ExtraColors
import org.elnix.dragonlauncher.utils.actions.ActionIcon
import org.elnix.dragonlauncher.utils.actions.actionColor

@Composable
fun AppPreviewTitle(
    offsetY: Dp,
    alpha: Float,
    icons: Map<String, ImageBitmap>,
    point: SwipePointSerializable,
    extraColors: ExtraColors,
    label: String,
    topPadding: Dp = 60.dp,
    showLabel: Boolean,
    showIcon: Boolean
) {
    val action = point.action
    if (showIcon || showLabel) {
        Box(
            Modifier
                .fillMaxWidth()
                .offset(y = offsetY)
                .padding(top = topPadding)
                .alpha(alpha),
            contentAlignment = Alignment.TopCenter
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (showIcon) {
                    ActionIcon(
                        point = point,
                        icons = icons,
                        modifier = Modifier.size(22.dp),
                    )
                }
                if (showLabel) {
                    Text(
                        text = label,
                        color = actionColor(action, extraColors),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

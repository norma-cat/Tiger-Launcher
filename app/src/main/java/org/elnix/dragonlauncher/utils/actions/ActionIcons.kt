package org.elnix.dragonlauncher.utils.actions

import android.content.Context
import android.graphics.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.graphics.createBitmap
import androidx.core.graphics.drawable.toBitmap
import org.elnix.dragonlauncher.R
import org.elnix.dragonlauncher.data.SwipeActionSerializable
import org.elnix.dragonlauncher.data.SwipePointSerializable
import org.elnix.dragonlauncher.data.helpers.CornerRadiusSerializable
import org.elnix.dragonlauncher.data.helpers.CustomIconSerializable
import org.elnix.dragonlauncher.data.helpers.IconType
import org.elnix.dragonlauncher.data.targetPackage
import org.elnix.dragonlauncher.ui.theme.LocalExtraColors
import org.elnix.dragonlauncher.utils.IMAGE_TAG
import org.elnix.dragonlauncher.utils.ImageUtils
import org.elnix.dragonlauncher.utils.loadShortcutIcon
import org.elnix.dragonlauncher.utils.logs.logD

@Composable
fun appIcon(
    packageName: String,
    icons: Map<String, ImageBitmap>? = null
): Painter {
    val cached = icons?.get(packageName)
    return if (cached != null) {
        BitmapPainter(cached)
    } else {
        painterResource(R.drawable.ic_app_default)
    }
}


@Composable
fun ActionIcon(
    action: SwipeActionSerializable? = null,
    point: SwipePointSerializable? = null,
    icons: Map<String, ImageBitmap>,
    modifier: Modifier = Modifier,
    size: Int = 48,
    showLaunchAppVectorGrid: Boolean = false
) {
    require(
        (action != null || point != null) &&
            !(action != null && point != null)
    ) {
        "ActionIcon requires either action or point"
    }

    val ctx = LocalContext.current
    val extraColors = LocalExtraColors.current

    val customIcon = point?.customIcon

    val baseSizeDp = customIcon?.sizeDp?.dp ?: size.dp

    val bitmap: ImageBitmap? = when {
        customIcon != null ->
            resolveCustomIconBitmap(
                icon = customIcon,
                sizePx = size
            )

        action is SwipeActionSerializable.LaunchApp && showLaunchAppVectorGrid ->
            loadDrawableResAsBitmap(ctx, R.drawable.ic_app_grid, size, size)

        else ->
            action?.let {
                actionIconBitmap(
                    icons = icons,
                    action = it,
                    ctx = ctx,
                    tintColor = actionColor(it, extraColors),
                    width = size,
                    height = size
                )
            }
    }

    ctx.logD(IMAGE_TAG, "ActionIcon - Resolved bitmap for action=${action?.let { actionLabel(it) }} point=${point?.id} : $bitmap")
    if (bitmap == null) return

    val shape = customIcon?.corners?.toRoundedCornerShape()

    Image(
        bitmap = bitmap,
        contentDescription = null,
        modifier = modifier
            .size(baseSizeDp)
            .thenIf(shape != null) {
                clip(shape!!)
            }
            .graphicsLayer {
                alpha = customIcon?.opacity ?: 1f
                rotationZ = customIcon?.rotationDeg ?: 0f
                scaleX = customIcon?.scaleX ?: 1f
                scaleY = customIcon?.scaleY ?: 1f

                if (customIcon?.shadowRadius != null) {
                    shadowElevation = customIcon.shadowRadius!!
                    translationX = customIcon.shadowOffsetX ?: 0f
                    translationY = customIcon.shadowOffsetY ?: 0f
                }
            }
            .thenIf(customIcon?.strokeWidth != null && shape != null) {
                border(
                    width = customIcon?.strokeWidth!!.dp,
                    color = Color(customIcon.strokeColor ?: 0x00000000),
                    shape = shape!!
                )
            }
            .thenIf(customIcon?.tint != null) {
                background(Color(customIcon?.tint!!))
            }
    )
}


fun actionIconBitmap(
    icons: Map<String, ImageBitmap>,
    action: SwipeActionSerializable,
    ctx: Context,
    tintColor: Color,
    width: Int = 48,
    height: Int = 48
): ImageBitmap {
    val bitmap = createUntintedBitmap(action, ctx, icons, width, height)
    return if (
        action is SwipeActionSerializable.LaunchApp ||
        action is SwipeActionSerializable.LaunchShortcut ||
        action is SwipeActionSerializable.OpenDragonLauncherSettings
    ) {
        bitmap
    } else {
        tintBitmap(bitmap, tintColor)
    }
}

private fun createUntintedBitmap(
    action: SwipeActionSerializable,
    ctx: Context,
    icons: Map<String, ImageBitmap>,
    width: Int,
    height: Int
): ImageBitmap {
    return when (action) {
        is SwipeActionSerializable.LaunchApp,
        is SwipeActionSerializable.LaunchShortcut -> {
            val pkg = action.targetPackage()!!

            if (action is SwipeActionSerializable.LaunchShortcut) {
                val shortcutIcon = loadShortcutIcon(ctx, pkg, action.shortcutId)
                if (shortcutIcon != null) return shortcutIcon
            }

            try {
                icons[pkg] ?: loadDrawableAsBitmap(
                    ctx.packageManager.getApplicationIcon(pkg),
                    width,
                    height
                )

            } catch (_: Exception) { // If the app was uninstalled, it could not reload it
                loadDrawableResAsBitmap(ctx, R.drawable.ic_app_default, width, height)
            }
        }

        is SwipeActionSerializable.OpenUrl ->
            loadDrawableResAsBitmap(ctx, R.drawable.ic_action_web, width, height)

        SwipeActionSerializable.NotificationShade ->
            loadDrawableResAsBitmap(ctx, R.drawable.ic_action_notification, width, height)

        SwipeActionSerializable.ControlPanel ->
            loadDrawableResAsBitmap(ctx, R.drawable.ic_action_grid, width, height)

        SwipeActionSerializable.OpenAppDrawer ->
            loadDrawableResAsBitmap(ctx, R.drawable.ic_action_drawer, width, height)

        SwipeActionSerializable.OpenDragonLauncherSettings ->
            loadDrawableResAsBitmap(ctx, R.drawable.dragon_launcher_foreground, width, height)

        SwipeActionSerializable.Lock -> loadDrawableResAsBitmap(ctx, R.drawable.ic_action_lock, width, height)
        is SwipeActionSerializable.OpenFile -> loadDrawableResAsBitmap(ctx, R.drawable.ic_action_open_file, width, height)
        SwipeActionSerializable.ReloadApps -> loadDrawableResAsBitmap(ctx, R.drawable.ic_action_reload, width, height)
        SwipeActionSerializable.OpenRecentApps -> loadDrawableResAsBitmap(ctx, R.drawable.ic_action_recent, width, height)
        is SwipeActionSerializable.OpenCircleNest -> loadDrawableResAsBitmap(ctx, R.drawable.ic_action_target, width, height)
        SwipeActionSerializable.GoParentNest -> loadDrawableResAsBitmap(ctx, R.drawable.ic_icon_go_parent_nest, width, height)
        is SwipeActionSerializable.OpenWidget -> loadDrawableResAsBitmap(ctx, R.drawable.ic_action_widgets, width, height)
    }
}


private fun tintBitmap(original: ImageBitmap, color: Color): ImageBitmap {
    val bitmap = createBitmap(original.width, original.height)
    val canvas = Canvas(bitmap)
    val paint = android.graphics.Paint().apply {
        colorFilter = android.graphics.PorterDuffColorFilter(
            color.toArgb(),
            android.graphics.PorterDuff.Mode.SRC_IN
        )
    }
    canvas.drawBitmap(original.asAndroidBitmap(), 0f, 0f, paint)
    return bitmap.asImageBitmap()
}


// Fallback: Create a simple colored square if all else fails
private fun createDefaultBitmap(
    width: Int,
    height: Int
): ImageBitmap {
    val bitmap = createBitmap(width, height)
    val canvas = Canvas(bitmap)
    canvas.drawColor(Color.Gray.toArgb())
    return bitmap.asImageBitmap()
}


fun loadDrawableResAsBitmap(
    context: Context,
    resId: Int,
    width: Int,
    height: Int
): ImageBitmap {
    val drawable = ContextCompat.getDrawable(context, resId)
        ?: return createDefaultBitmap(width, height)

    return loadDrawableAsBitmap(drawable, width, height)
}

@Suppress("SameParameterValue")
fun loadDrawableAsBitmap(
    drawable: android.graphics.drawable.Drawable,
    width: Int,
    height: Int
): ImageBitmap {
    // Adaptive icon support
    val bmp = if (drawable is android.graphics.drawable.AdaptiveIconDrawable) {
        val bitmap = createBitmap(width, height)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        bitmap
    } else {
        drawable.toBitmap(width, height)
    }
    return bmp.asImageBitmap()
}


private fun resolveCustomIconBitmap(
    icon: CustomIconSerializable,
    sizePx: Int
): ImageBitmap? {
    return when (icon.type) {
        IconType.BITMAP,
        IconType.ICON_PACK ->
            ImageUtils.base64ToImageBitmap(icon.source)

        IconType.TEXT ->
            icon.source?.let {
                ImageUtils.textToBitmap(
                    text = it,
                    sizePx = sizePx
                )
            }

        IconType.SHAPE ->
            null // Shape rendering is not supported yet

        null -> null
    }
}


private fun CornerRadiusSerializable.toRoundedCornerShape(): RoundedCornerShape {
    return RoundedCornerShape(
        topStart = (topLeft ?: 0f).dp,
        topEnd = (topRight ?: 0f).dp,
        bottomEnd = (bottomRight ?: 0f).dp,
        bottomStart = (bottomLeft ?: 0f).dp
    )
}

private inline fun Modifier.thenIf(
    condition: Boolean,
    block: Modifier.() -> Modifier
): Modifier = if (condition) this.block() else this

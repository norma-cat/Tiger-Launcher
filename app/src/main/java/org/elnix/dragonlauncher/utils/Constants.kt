package org.elnix.dragonlauncher.utils

import org.elnix.dragonlauncher.data.SwipeActionSerializable
import org.elnix.dragonlauncher.ui.SETTINGS

const val obtainiumPackageName = "dev.imranr.obtainium.fdroid"


/** List of routes that the routes killer ignores when the user leave the app for too long, usually files pickers */
val ignoredReturnRoutes = listOf(
    SETTINGS.BACKUP,
    SETTINGS.WALLPAPER,
    SETTINGS.FLOATING_APPS
)



/* Themes loader utils */
const val themesDir = "themes"
val imageExts = listOf("png", "jpg", "jpeg", "webp")



val defaultChoosableActions = listOf(
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

const val TAG = "DragonLauncherDebug"
const val BACKUP_TAG = "SettingsBackupManager"


const val TAGSwipe = "SwipeDebug"
const val WIDGET_TAG = "WidgetsDebug"

const val FLOATING_APPS_TAG = "FloatingAppsDebug"

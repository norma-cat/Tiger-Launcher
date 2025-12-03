package org.elnix.dragonlauncher.ui.drawer

import org.elnix.dragonlauncher.data.SwipeActionSerializable

data class AppModel(
    val name: String,
    val packageName: String,
    val isEnabled: Boolean,
    val isSystem: Boolean,
    val isWorkProfile: Boolean,
    val settings: Map<String, Any> = emptyMap()
) {
    val action = SwipeActionSerializable.LaunchApp(packageName)
}

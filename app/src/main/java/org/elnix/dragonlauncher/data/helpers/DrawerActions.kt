package org.elnix.dragonlauncher.data.helpers

import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.runtime.Composable
import org.elnix.dragonlauncher.R

enum class DrawerActions { CLOSE, TOGGLE_KB, NONE }

@Composable
fun drawerActionIcon(action: DrawerActions) = when (action) {
    DrawerActions.CLOSE -> Icons.Default.Close
    DrawerActions.TOGGLE_KB -> Icons.Default.Keyboard
    DrawerActions.NONE -> Icons.Default.RadioButtonUnchecked
}

fun drawerActionsLabel(ctx: Context,action: DrawerActions) = when (action) {
    DrawerActions.CLOSE -> ctx.getString(R.string.close_drawer)
    DrawerActions.TOGGLE_KB -> ctx.getString(R.string.toggle_kb)
    DrawerActions.NONE -> ""
}

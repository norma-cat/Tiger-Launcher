package org.elnix.dragonlauncher.data

import android.content.Context

abstract class BaseSettingsStore {
    abstract val name: String
    abstract suspend fun resetAll(ctx: Context)
}

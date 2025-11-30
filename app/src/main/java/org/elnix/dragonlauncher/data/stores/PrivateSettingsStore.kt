package org.elnix.dragonlauncher.data.stores

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.elnix.dragonlauncher.data.privateSettingsStore


object PrivateSettingsStore {

    private data class PrivateBackup (
        val hasSeenWelcome: Boolean = false,
        val hasInitialized: Boolean = false
    )

    private val HAS_SEEN_WELCOME = booleanPreferencesKey("has_seen_welcome")
    private val HAS_INITIALIZED = booleanPreferencesKey("has_initialized")
    fun getHasSeenWelcome(ctx: Context): Flow<Boolean> =
        ctx.privateSettingsStore.data.map { it[HAS_SEEN_WELCOME] ?: false }
    suspend fun setHasSeenWelcome(ctx: Context, enabled: Boolean) {
        ctx.privateSettingsStore.edit { it[HAS_SEEN_WELCOME] = enabled }
    }

    fun getHasInitialized(ctx: Context): Flow<Boolean> =
        ctx.privateSettingsStore.data.map { it[HAS_INITIALIZED] ?: false }
    suspend fun setHasInitialized(ctx: Context, enabled: Boolean) {
        ctx.privateSettingsStore.edit { it[HAS_INITIALIZED] = enabled }
    }

    suspend fun resetAll(ctx: Context) {
        ctx.privateSettingsStore.edit { preferences ->
            preferences.remove(HAS_SEEN_WELCOME)
            preferences.remove(HAS_INITIALIZED)
        }
    }

    suspend fun getAll(ctx: Context): Map<String, String> {
        val prefs = ctx.privateSettingsStore.data.first()
        val defaults = PrivateBackup()

        return buildMap {
            fun putIfNonDefault(key: String, value: Any?, default: Any?) {
                if (value != null && value != default) {
                    put(key, value.toString())
                }
            }

            putIfNonDefault(HAS_SEEN_WELCOME.name, prefs[HAS_SEEN_WELCOME], defaults.hasSeenWelcome)
            putIfNonDefault(HAS_INITIALIZED.name, prefs[HAS_INITIALIZED], defaults.hasInitialized)
        }
    }

    suspend fun setAll(ctx: Context, backup: Map<String, Boolean>) {
        ctx.privateSettingsStore.edit { prefs ->
            backup[HAS_SEEN_WELCOME.name]?.let { enabled ->
                prefs[HAS_SEEN_WELCOME] = enabled
            }

            backup[HAS_INITIALIZED.name]?.let { enabled ->
                prefs[HAS_INITIALIZED] = enabled
            }
        }
    }
}
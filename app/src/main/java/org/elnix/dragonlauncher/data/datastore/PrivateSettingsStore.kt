package org.elnix.dragonlauncher.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.privateSettingsStore by preferencesDataStore("privateSettingsStore")


object PrivateSettingsStore {
    private val HAS_SEEN_WELCOME = booleanPreferencesKey("has_seen_welcome")
    fun getHasSeenWelcome(ctx: Context): Flow<Boolean> =
        ctx.privateSettingsStore.data.map { it[HAS_SEEN_WELCOME] ?: false }
    suspend fun setHasSeenWelcome(ctx: Context, enabled: Boolean) {
        ctx.privateSettingsStore.edit { it[HAS_SEEN_WELCOME] = enabled }
    }
}
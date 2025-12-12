package org.elnix.dragonlauncher.data.stores

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.elnix.dragonlauncher.data.BackupTypeException
import org.elnix.dragonlauncher.data.BaseSettingsStore
import org.elnix.dragonlauncher.data.debugDatastore

object DebugSettingsStore : BaseSettingsStore() {

    override val name: String = "Debug"

    // -------------------------------------------------------------------------
    // Backup data class
    // -------------------------------------------------------------------------
    private data class DebugSettingsBackup(
        val debugEnabled: Boolean = false,
        val debugInfos: Boolean = false,
        val forceAppLanguageSelector: Boolean = false
    )

    private val defaults = DebugSettingsBackup()

    // -------------------------------------------------------------------------
    // Keys object for safer reference
    // -------------------------------------------------------------------------
    private object Keys {
        const val DEBUG_ENABLED = "debugEnabled"
        const val DEBUG_INFOS = "debugInfos"
        const val FORCE_APP_LANGUAGE_SELECTOR = "forceAppLanguageSelector"
    }

    private val DEBUG_ENABLED = booleanPreferencesKey(Keys.DEBUG_ENABLED)
    private val DEBUG_INFOS = booleanPreferencesKey(Keys.DEBUG_INFOS)
    private val FORCE_APP_LANGUAGE_SELECTOR = booleanPreferencesKey(Keys.FORCE_APP_LANGUAGE_SELECTOR)

    // -------------------------------------------------------------------------
    // Accessors + Mutators
    // -------------------------------------------------------------------------
    fun getDebugEnabled(ctx: Context): Flow<Boolean> =
        ctx.debugDatastore.data.map { prefs ->
            prefs[DEBUG_ENABLED] ?: defaults.debugEnabled
        }

    suspend fun setDebugEnabled(ctx: Context, enabled: Boolean) {
        ctx.debugDatastore.edit { it[DEBUG_ENABLED] = enabled }
    }

    fun getDebugInfos(ctx: Context): Flow<Boolean> =
        ctx.debugDatastore.data.map { prefs ->
            prefs[DEBUG_INFOS] ?: defaults.debugInfos
        }

    suspend fun setDebugInfos(ctx: Context, enabled: Boolean) {
        ctx.debugDatastore.edit { it[DEBUG_INFOS] = enabled }
    }

    fun getForceAppLanguageSelector(ctx: Context): Flow<Boolean> =
        ctx.debugDatastore.data.map { prefs ->
            prefs[FORCE_APP_LANGUAGE_SELECTOR] ?: defaults.forceAppLanguageSelector
        }

    suspend fun setForceAppLanguageSelector(ctx: Context, enabled: Boolean) {
        ctx.debugDatastore.edit { it[FORCE_APP_LANGUAGE_SELECTOR] = enabled }
    }

    // -------------------------------------------------------------------------
    // Reset
    // -------------------------------------------------------------------------
    override suspend fun resetAll(ctx: Context) {
        ctx.debugDatastore.edit { prefs ->
            prefs.remove(DEBUG_ENABLED)
            prefs.remove(DEBUG_INFOS)
            prefs.remove(FORCE_APP_LANGUAGE_SELECTOR)
        }
    }

    // -------------------------------------------------------------------------
    // Backup export
    // -------------------------------------------------------------------------
    suspend fun getAll(ctx: Context): Map<String, Boolean> {
        val prefs = ctx.debugDatastore.data.first()

        return buildMap {

            fun putIfNonDefault(key: String, value: Boolean?, defaultVal: Boolean) {
                if (value != null && value != defaultVal) {
                    put(key, value)
                }
            }

            putIfNonDefault(Keys.DEBUG_ENABLED, prefs[DEBUG_ENABLED], defaults.debugEnabled)
            putIfNonDefault(Keys.DEBUG_INFOS, prefs[DEBUG_INFOS], defaults.debugInfos)
            putIfNonDefault(Keys.FORCE_APP_LANGUAGE_SELECTOR, prefs[FORCE_APP_LANGUAGE_SELECTOR], defaults.forceAppLanguageSelector)
        }
    }

    // -------------------------------------------------------------------------
    // Backup import
    // -------------------------------------------------------------------------
    suspend fun setAll(ctx: Context, backup: Map<String, Any?>) {

        fun getBooleanStrict(key: String): Boolean {
            val v = backup[key] ?: return when (key) {
                Keys.DEBUG_ENABLED -> defaults.debugEnabled
                Keys.DEBUG_INFOS -> defaults.debugInfos
                Keys.FORCE_APP_LANGUAGE_SELECTOR -> defaults.forceAppLanguageSelector
                else -> throw BackupTypeException(key, "Boolean", null, null)
            }
            return v as? Boolean ?: throw BackupTypeException(
                key = key,
                expected = "Boolean",
                actual = v::class.simpleName,
                value = v
            )
        }

        val backupValues = DebugSettingsBackup(
            debugEnabled = getBooleanStrict(Keys.DEBUG_ENABLED),
            debugInfos = getBooleanStrict(Keys.DEBUG_INFOS),
            forceAppLanguageSelector = getBooleanStrict(Keys.FORCE_APP_LANGUAGE_SELECTOR)
        )

        ctx.debugDatastore.edit { prefs ->
            prefs[DEBUG_ENABLED] = backupValues.debugEnabled
            prefs[DEBUG_INFOS] = backupValues.debugInfos
            prefs[FORCE_APP_LANGUAGE_SELECTOR] = backupValues.forceAppLanguageSelector
        }
    }
}

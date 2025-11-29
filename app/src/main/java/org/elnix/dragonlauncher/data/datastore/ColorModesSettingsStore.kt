package org.elnix.dragonlauncher.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.elnix.dragonlauncher.data.helpers.ColorPickerMode

val Context.colorDatastore by preferencesDataStore("colorDatastore")
object ColorModesSettingsStore {
    private val COLOR_PICKER_MODE = stringPreferencesKey("color_picker_mode")
    fun getColorPickerMode(ctx: Context): Flow<ColorPickerMode> =
        ctx.colorDatastore.data.map { prefs ->
            prefs[COLOR_PICKER_MODE]?.let { ColorPickerMode.valueOf(it) }
                ?: ColorPickerMode.SLIDERS
        }
    suspend fun setColorPickerMode(ctx: Context, state: ColorPickerMode) {
        ctx.colorDatastore.edit { it[COLOR_PICKER_MODE] = state.name}
    }


    suspend fun resetAll(ctx: Context) {
        ctx.colorDatastore.edit { prefs ->
            prefs.remove(COLOR_PICKER_MODE)
        }
    }

    suspend fun getAll(ctx: Context): Map<String, String> {
        val prefs = ctx.colorDatastore.data.first()
        return buildMap {
            prefs[COLOR_PICKER_MODE]?.let { put(COLOR_PICKER_MODE.name, it) }
        }
    }

    suspend fun setAll(ctx: Context, data: Map<String, String>) {
        ctx.colorDatastore.edit { prefs ->
            data[COLOR_PICKER_MODE.name]?.let { prefs[COLOR_PICKER_MODE] = it }
        }
    }
}
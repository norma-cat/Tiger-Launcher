package org.elnix.dragonlauncher.data.stores

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.elnix.dragonlauncher.data.SwipeJson
import org.elnix.dragonlauncher.data.SwipePointSerializable
import org.elnix.dragonlauncher.data.swipeDataStore

object SwipeSettingsStore {

//    data class SwipeBackup(
//        val pointsJson: String? = null
//    )

    private val POINTS = stringPreferencesKey("points_json")

    suspend fun getPoints(ctx: Context): List<SwipePointSerializable> {
        return ctx.swipeDataStore.data
            .map { prefs ->
                prefs[POINTS]?.let { SwipeJson.decode(it) } ?: emptyList()
            }
            .first()
    }

    fun getPointsFlow(ctx: Context) =
        ctx.swipeDataStore.data.map { prefs ->
            prefs[POINTS]?.let { SwipeJson.decode(it) } ?: emptyList()
        }

    suspend fun save(ctx: Context, points: List<SwipePointSerializable>) {
        ctx.swipeDataStore.edit { prefs ->
            prefs[POINTS] = SwipeJson.encode(points)
        }
    }


    suspend fun resetAll(ctx: Context) {
        ctx.swipeDataStore.edit { prefs ->
            prefs.remove(POINTS)
        }
    }

    suspend fun getAll(ctx: Context): List<SwipePointSerializable> {
        val prefs = ctx.swipeDataStore.data.first()
        val raw = prefs[POINTS] ?: return emptyList()
        return SwipeJson.decode(raw)
    }

    // Removed setAll cause i'm using the save function
}

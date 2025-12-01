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

    data class SwipeBackup(
        val pointsJson: String? = null
    )

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

    suspend fun getAll(ctx: Context): Map<String, Any> {
        val prefs = ctx.swipeDataStore.data.first()

        val points = prefs[POINTS]?.let { json ->
            SwipeJson.decode(json)
        } ?: emptyList()

        return mapOf(
            "points" to points
        )
    }

    suspend fun setAll(ctx: Context, backup: Map<String, Any>) {
        ctx.swipeDataStore.edit { prefs ->
            val pointsList = backup["points"] as? List<*>
            if (pointsList != null) {
                // Re-encode list back to string for DataStore
                @Suppress("UNCHECKED_CAST")
                val typed = pointsList as List<SwipePointSerializable>
                prefs[POINTS] = SwipeJson.encode(typed)
            } else {
                prefs.remove(POINTS)
            }
        }
    }

}

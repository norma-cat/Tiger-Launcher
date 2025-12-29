package org.elnix.dragonlauncher.utils

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import org.elnix.dragonlauncher.data.DataStoreName
import org.elnix.dragonlauncher.data.SwipeJson
import org.elnix.dragonlauncher.data.stores.BackupSettingsStore
import org.elnix.dragonlauncher.data.stores.BehaviorSettingsStore
import org.elnix.dragonlauncher.data.stores.ColorModesSettingsStore
import org.elnix.dragonlauncher.data.stores.ColorSettingsStore
import org.elnix.dragonlauncher.data.stores.DebugSettingsStore
import org.elnix.dragonlauncher.data.stores.DrawerSettingsStore
import org.elnix.dragonlauncher.data.stores.LanguageSettingsStore
import org.elnix.dragonlauncher.data.stores.StatusBarSettingsStore
import org.elnix.dragonlauncher.data.stores.SwipeSettingsStore
import org.elnix.dragonlauncher.data.stores.UiSettingsStore
import org.elnix.dragonlauncher.data.stores.WallpaperSettingsStore
import org.elnix.dragonlauncher.data.stores.WorkspaceSettingsStore
import org.json.JSONArray
import org.json.JSONObject
import java.io.FileOutputStream

object SettingsBackupManager {

    private const val TAG = "SettingsBackupManager"

    /**
     * Automatic backup to pre-selected file
     */
    // In SettingsBackupManager.triggerBackup()
    suspend fun triggerBackup(ctx: Context) {
        if (!BackupSettingsStore.getAutoBackupEnabled(ctx).first()) {
            Log.w(TAG, "Auto-backup disabled")
            return
        }

        try {
            val uriString = BackupSettingsStore.getAutoBackupUri(ctx).first()
            if (uriString.isNullOrBlank()) {  // FIXED: catches "" too
                Log.w(TAG, "No backup URI set")
                return
            }

            val uri = uriString.toUri()
            val path = getFilePathFromUri(ctx, uri)

            if (!ctx.hasUriReadWritePermission(uri)) {
                Log.e(TAG, "URI permission expired!")
                ctx.showToast("Auto-backup URI expired. Please reselect file.")
                return
            }

            val selectedStores = BackupSettingsStore.getBackupStores(ctx).first()
                .mapNotNull { storeValue -> DataStoreName.entries.find { it.value == storeValue && it.backupKey != null } }

            exportSettings(ctx, uri, selectedStores)

            BackupSettingsStore.setLastBackupTime(ctx)
            Log.i(TAG, "Auto-backup completed to $path")

        } catch (e: Exception) {
            Log.e(TAG, "Auto-backup failed", e)
            if (e.message?.contains("permission") == true) {
                ctx.showToast("URI permission lost. Reselect backup file.")
            }
        }
    }


    /**
     * Exports only the requested stores.
     * @param requestedStores List of DataStoreName objects
     */
    suspend fun exportSettings(ctx: Context, uri: Uri, requestedStores: List<DataStoreName>) {
        try {
            val json = JSONObject().apply {
                requestedStores.forEach { store ->
                    when (store) {
                        DataStoreName.SWIPE -> {
                            val pointsList = SwipeSettingsStore.getPoints(ctx)
                            val nestsList = SwipeSettingsStore.getNests(ctx)

                            if (pointsList.isNotEmpty() || nestsList.isNotEmpty()) {
                                val swipeObj = JSONObject().apply {
                                    if (pointsList.isNotEmpty()) {
                                        put("points", JSONArray(SwipeJson.encodePointsPretty(pointsList)))
                                    }
                                    if (nestsList.isNotEmpty()) {
                                        put("nests", JSONArray(SwipeJson.encodeNestsPretty(nestsList))) // Add encodeNestsPretty if needed
                                    }
                                }
                                put(store.backupKey!!, swipeObj)
                            }
                        }

                        DataStoreName.DRAWER -> DrawerSettingsStore.getAll(ctx).takeIf { it.isNotEmpty() }?.let {
                            put(store.backupKey!!, JSONObject(it))
                        }
                        DataStoreName.COLOR_MODE -> ColorModesSettingsStore.getAll(ctx).takeIf { it.isNotEmpty() }?.let {
                            put(store.backupKey!!, JSONObject(it))
                        }
                        DataStoreName.COLOR -> ColorSettingsStore.getAll(ctx).takeIf { it.isNotEmpty() }?.let {
                            put(store.backupKey!!, JSONObject(it))
                        }
                        DataStoreName.LANGUAGE -> LanguageSettingsStore.getAll(ctx).takeIf { it.isNotEmpty() }?.let {
                            put(store.backupKey!!, JSONObject(it))
                        }
                        DataStoreName.UI -> UiSettingsStore.getAll(ctx).takeIf { it.isNotEmpty() }?.let {
                            put(store.backupKey!!, JSONObject(it))
                        }
                        DataStoreName.DEBUG -> DebugSettingsStore.getAll(ctx).takeIf { it.isNotEmpty() }?.let {
                            put(store.backupKey!!, JSONObject(it))
                        }
                        DataStoreName.WORKSPACES -> {
                            val obj = WorkspaceSettingsStore.getAll(ctx)
                            if (obj.length() > 0) {
                                put(store.backupKey!!, obj)
                            }
                        }
                        DataStoreName.BEHAVIOR -> BehaviorSettingsStore.getAll(ctx).takeIf { it.isNotEmpty() }?.let {
                            put(store.backupKey!!, JSONObject(it))
                        }
                        DataStoreName.BACKUP -> BackupSettingsStore.getAll(ctx).takeIf { it.isNotEmpty() }?.let {
                            put(store.backupKey!!, JSONObject(it))
                        }
                        DataStoreName.WALLPAPER -> WallpaperSettingsStore.getAll(ctx).takeIf { it.isNotEmpty() }?.let {
                            put(store.backupKey!!, JSONObject(it))
                        }
                        DataStoreName.STATUS_BAR -> StatusBarSettingsStore.getAll(ctx).takeIf { it.isNotEmpty() }?.let {
                            put(store.backupKey!!, JSONObject(it))
                        }

                        // Those 2 aren't meant to be backupable
                        DataStoreName.APPS -> {}
                        DataStoreName.PRIVATE_SETTINGS -> {}
                    }
                }
            }

            Log.d(TAG, "Generated JSON: $json")

            // Force overwriting file before printing json, to avoid strange corruption with not truncating json
            withContext(Dispatchers.IO) {
                ctx.contentResolver.openFileDescriptor(uri, "wt")?.use { pfd ->
                    FileOutputStream(pfd.fileDescriptor).use { fos ->
                        fos.channel.truncate(0)
                        fos.write(json.toString(2).toByteArray())
                        fos.flush()
                    }
                } ?: run {
                    Log.e(TAG, "Failed to open FileDescriptor - URI permission expired!")
                    throw IllegalStateException("Cannot write to URI - permission expired")
                }
            }


            Log.i(TAG, "Export completed successfully.")
        } catch (e: Exception) {
            Log.e(TAG, "Error during export", e)
            throw e
        }
    }

    /**
     * Imports settings directly from parsed JSON (no file needed).
     * @param requestedStores List of DataStoreName objects to restore.
     */
    suspend fun importSettingsFromJson(ctx: Context, jsonObj: JSONObject, requestedStores: List<DataStoreName>) {
        try {
            withContext(Dispatchers.IO) {
                requestedStores.forEach { store ->
                    when (store) {
                        DataStoreName.SWIPE -> {
                            // Try NEW format first (has nests/points objects) (new key: "new_actions")
                            val swipeObj = jsonObj.optJSONObject(store.backupKey)

                            if (swipeObj != null) {
                                importNewSwipeFormat(swipeObj, ctx)
                                return@forEach // Success, skip old format
                            }

                            // Fallback to old format (key was "actions")
                            jsonObj.optJSONArray("actions")?.let { jsonArr ->
                                importOldSwipeFormat(jsonArr.toString(2), ctx)
                            }
                        }
                        DataStoreName.DRAWER -> jsonObj.optJSONObject(store.backupKey)?.let {
                            DrawerSettingsStore.setAll(ctx, jsonToStringMap(it))
                        }
                        DataStoreName.COLOR_MODE -> jsonObj.optJSONObject(store.backupKey)?.let {
                            ColorModesSettingsStore.setAll(ctx, jsonToStringMap(it))
                        }
                        DataStoreName.COLOR -> jsonObj.optJSONObject(store.backupKey)?.let {
                            ColorSettingsStore.setAll(ctx, jsonToIntMap(it))
                        }
                        DataStoreName.LANGUAGE -> jsonObj.optJSONObject(store.backupKey)?.let {
                            LanguageSettingsStore.setAll(ctx, jsonToStringMap(it))
                        }
                        DataStoreName.UI -> jsonObj.optJSONObject(store.backupKey)?.let {
                            UiSettingsStore.setAll(ctx, jsonToStringMap(it))
                        }
                        DataStoreName.DEBUG -> jsonObj.optJSONObject(store.backupKey)?.let {
                            DebugSettingsStore.setAll(ctx, jsonToBooleanMap(it))
                        }
                        DataStoreName.WORKSPACES -> jsonObj.optJSONObject(store.backupKey)?.let {
                            WorkspaceSettingsStore.setAll(ctx, it)
                        }
                        DataStoreName.BEHAVIOR -> jsonObj.optJSONObject(store.backupKey)?.let {
                            BehaviorSettingsStore.setAll(ctx, jsonToStringMap(it))
                        }
                        DataStoreName.BACKUP -> jsonObj.optJSONObject(store.backupKey)?.let {
                            BackupSettingsStore.setAll(ctx, jsonToStringMap(it))
                        }
                        DataStoreName.WALLPAPER -> jsonObj.optJSONObject(store.backupKey)?.let {
                            WallpaperSettingsStore.setAll(ctx, jsonToStringMap(it))
                        }
                        DataStoreName.STATUS_BAR -> jsonObj.optJSONObject(store.backupKey)?.let {
                            StatusBarSettingsStore.setAll(ctx, jsonToStringMap(it))
                        }

                        DataStoreName.APPS -> {}
                        DataStoreName.PRIVATE_SETTINGS -> {}
                    }
                }
            }
            Log.i(TAG, "Import from JSON completed successfully.")
        } catch (e: Exception) {
            Log.e(TAG, "Error during JSON import", e)
            throw e
        }
    }


    private fun jsonToBooleanMap(obj: JSONObject) = buildMap {
        obj.keys().forEach { key -> put(key, obj.optBoolean(key)) }
    }

    private fun jsonToIntMap(obj: JSONObject) = buildMap {
        obj.keys().forEach { key -> put(key, obj.optInt(key)) }
    }

    private fun jsonToStringMap(obj: JSONObject) = buildMap {
        obj.keys().forEach { key -> put(key, obj.optString(key)) }
    }
}
private suspend fun importNewSwipeFormat(swipeObj: JSONObject, ctx: Context) {
    swipeObj.optJSONArray("points")?.let { jsonArr ->
        val pointsJson = jsonArr.toString(2)
        Log.d("Import", "New format points: $pointsJson")
        val pointsList = SwipeJson.decodePoints(pointsJson)
        SwipeSettingsStore.savePoints(ctx, pointsList)
    }

    swipeObj.optJSONArray("nests")?.let { jsonArr ->
        val nestsJson = jsonArr.toString(2)
        Log.d("Import", "New format nests: $nestsJson")
        val nestsList = SwipeJson.decodeNests(nestsJson)
        SwipeSettingsStore.saveNests(ctx, nestsList)
    }
}

private suspend fun importOldSwipeFormat(jsonString: String, ctx: Context) {
    Log.d("Import", "Old format detected: $jsonString")
    val pointsList = SwipeJson.decode(jsonString) // Old decoder
    SwipeSettingsStore.savePoints(ctx, pointsList) // Save to new format, for upgrade
    Log.d("Import", "Imported old points into new format: $pointsList")
}

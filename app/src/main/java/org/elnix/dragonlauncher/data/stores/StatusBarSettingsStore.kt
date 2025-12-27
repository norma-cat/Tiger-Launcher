package org.elnix.dragonlauncher.data.stores

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.elnix.dragonlauncher.data.BaseSettingsStore
import org.elnix.dragonlauncher.data.getBooleanStrict
import org.elnix.dragonlauncher.data.getIntStrict
import org.elnix.dragonlauncher.data.getStringStrict
import org.elnix.dragonlauncher.data.putIfNonDefault
import org.elnix.dragonlauncher.data.statusBarDatastore
import org.elnix.dragonlauncher.ui.theme.AmoledDefault

object StatusBarSettingsStore : BaseSettingsStore() {

    override val name: String = "Status Bar"

    private data class SettingsBackup(
        val showStatusBar: Boolean = true,
        val barBackgroundColor: Int = Color.Transparent.toArgb(),
        val barTextColor: Int = AmoledDefault.OnBackground.toArgb(),
        val showTime: Boolean = true,
        val showDate: Boolean = false,
        val timeFormatter: String = "HH:mm:ss",
        val dateFormatter: String = "MMM dd",
        val showNotifications: Boolean = false,
        val showBattery: Boolean = true,
        val showConnectivity: Boolean = false
    )

    private val defaults = SettingsBackup()

    private object Keys {
        val SHOW_STATUS_BAR = booleanPreferencesKey(SettingsBackup::showStatusBar.name)
        val BAR_BACKGROUND_COLOR = intPreferencesKey(SettingsBackup::barBackgroundColor.name)
        val BAR_TEXT_COLOR = intPreferencesKey(SettingsBackup::barTextColor.name)
        val SHOW_TIME = booleanPreferencesKey(SettingsBackup::showTime.name)
        val SHOW_DATE = booleanPreferencesKey(SettingsBackup::showDate.name)
        val TIME_FORMATTER = stringPreferencesKey("timeFormatter")
        val DATE_FORMATTER = stringPreferencesKey("dateFormatter")
        val SHOW_NOTIFICATIONS = booleanPreferencesKey(SettingsBackup::showNotifications.name)
        val SHOW_BATTERY = booleanPreferencesKey(SettingsBackup::showBattery.name)
        val SHOW_CONNECTIVITY = booleanPreferencesKey(SettingsBackup::showConnectivity.name)

        val ALL = listOf(
            SHOW_STATUS_BAR,
            BAR_BACKGROUND_COLOR,
            BAR_TEXT_COLOR,
            SHOW_TIME,
            SHOW_DATE,
            TIME_FORMATTER,
            DATE_FORMATTER,
            SHOW_NOTIFICATIONS,
            SHOW_BATTERY,
            SHOW_CONNECTIVITY
        )
    }

    /* ───────────── visibility ───────────── */

    fun getShowStatusBar(ctx: Context): Flow<Boolean> =
        ctx.statusBarDatastore.data.map { it[Keys.SHOW_STATUS_BAR] ?: defaults.showStatusBar }

    suspend fun setShowStatusBar(ctx: Context, value: Boolean) {
        ctx.statusBarDatastore.edit { it[Keys.SHOW_STATUS_BAR] = value }
    }

    /* ───────────── colors ───────────── */

    fun getBarBackgroundColor(ctx: Context): Flow<Color> =
        ctx.statusBarDatastore.data.map {
            Color(it[Keys.BAR_BACKGROUND_COLOR] ?: defaults.barBackgroundColor)
        }

    suspend fun setBarBackgroundColor(ctx: Context, color: Color) {
        ctx.statusBarDatastore.edit {
            it[Keys.BAR_BACKGROUND_COLOR] = color.toArgb()
        }
    }

    fun getBarTextColor(ctx: Context): Flow<Color> =
        ctx.statusBarDatastore.data.map {
            Color(it[Keys.BAR_TEXT_COLOR] ?: defaults.barTextColor)
        }

    suspend fun setBarTextColor(ctx: Context, color: Color) {
        ctx.statusBarDatastore.edit {
            it[Keys.BAR_TEXT_COLOR] = color.toArgb()
        }
    }

    /* ───────────── existing flags ───────────── */

    fun getShowTime(ctx: Context): Flow<Boolean> =
        ctx.statusBarDatastore.data.map { it[Keys.SHOW_TIME] ?: defaults.showTime }

    suspend fun setShowTime(ctx: Context, value: Boolean) {
        ctx.statusBarDatastore.edit { it[Keys.SHOW_TIME] = value }
    }

    fun getShowDate(ctx: Context): Flow<Boolean> =
        ctx.statusBarDatastore.data.map { it[Keys.SHOW_DATE] ?: defaults.showDate }

    suspend fun setShowDate(ctx: Context, value: Boolean) {
        ctx.statusBarDatastore.edit { it[Keys.SHOW_DATE] = value }
    }


    fun getTimeFormatter(ctx: Context): Flow<String> =
        ctx.statusBarDatastore.data.map {
            it[Keys.TIME_FORMATTER] ?: defaults.timeFormatter
        }

    suspend fun setTimeFormatter(ctx: Context, formatter: String) {
        ctx.statusBarDatastore.edit { it[Keys.TIME_FORMATTER] = formatter }
    }

    fun getDateFormatter(ctx: Context): Flow<String> =
        ctx.statusBarDatastore.data.map {
            it[Keys.DATE_FORMATTER] ?: defaults.dateFormatter
        }

    suspend fun setDateFormatter(ctx: Context, formatter: String) {
        ctx.statusBarDatastore.edit { it[Keys.DATE_FORMATTER] = formatter }
    }

    fun getShowNotifications(ctx: Context): Flow<Boolean> =
        ctx.statusBarDatastore.data.map { it[Keys.SHOW_NOTIFICATIONS] ?: defaults.showNotifications }

    suspend fun setShowNotifications(ctx: Context, value: Boolean) {
        ctx.statusBarDatastore.edit { it[Keys.SHOW_NOTIFICATIONS] = value }
    }

    fun getShowBattery(ctx: Context): Flow<Boolean> =
        ctx.statusBarDatastore.data.map { it[Keys.SHOW_BATTERY] ?: defaults.showBattery }

    suspend fun setShowBattery(ctx: Context, value: Boolean) {
        ctx.statusBarDatastore.edit { it[Keys.SHOW_BATTERY] = value }
    }

    fun getShowConnectivity(ctx: Context): Flow<Boolean> =
        ctx.statusBarDatastore.data.map { it[Keys.SHOW_CONNECTIVITY] ?: defaults.showConnectivity }

    suspend fun setShowConnectivity(ctx: Context, value: Boolean) {
        ctx.statusBarDatastore.edit { it[Keys.SHOW_CONNECTIVITY] = value }
    }

    /* ───────────── reset / backup ───────────── */

    override suspend fun resetAll(ctx: Context) {
        ctx.statusBarDatastore.edit { prefs ->
            Keys.ALL.forEach { prefs.remove(it) }
        }
    }

    suspend fun getAll(ctx: Context): Map<String, Any> {
        val prefs = ctx.statusBarDatastore.data.first()

        return buildMap {
            putIfNonDefault(Keys.SHOW_STATUS_BAR, prefs[Keys.SHOW_STATUS_BAR], defaults.showStatusBar)
            putIfNonDefault(Keys.BAR_BACKGROUND_COLOR, prefs[Keys.BAR_BACKGROUND_COLOR], defaults.barBackgroundColor)
            putIfNonDefault(Keys.BAR_TEXT_COLOR, prefs[Keys.BAR_TEXT_COLOR], defaults.barTextColor)
            putIfNonDefault(Keys.SHOW_TIME, prefs[Keys.SHOW_TIME], defaults.showTime)
            putIfNonDefault(Keys.SHOW_DATE, prefs[Keys.SHOW_DATE], defaults.showDate)
            putIfNonDefault(Keys.TIME_FORMATTER, prefs[Keys.TIME_FORMATTER], defaults.timeFormatter)
            putIfNonDefault(Keys.DATE_FORMATTER, prefs[Keys.DATE_FORMATTER], defaults.dateFormatter)
            putIfNonDefault(Keys.SHOW_NOTIFICATIONS, prefs[Keys.SHOW_NOTIFICATIONS], defaults.showNotifications)
            putIfNonDefault(Keys.SHOW_BATTERY, prefs[Keys.SHOW_BATTERY], defaults.showBattery)
            putIfNonDefault(Keys.SHOW_CONNECTIVITY, prefs[Keys.SHOW_CONNECTIVITY], defaults.showConnectivity)
        }
    }

    suspend fun setAll(ctx: Context, backup: Map<String, Any?>) {
        ctx.statusBarDatastore.edit { prefs ->
            prefs[Keys.SHOW_STATUS_BAR] =
                getBooleanStrict(backup, Keys.SHOW_STATUS_BAR, defaults.showStatusBar)

            prefs[Keys.BAR_BACKGROUND_COLOR] =
                getIntStrict(backup, Keys.BAR_BACKGROUND_COLOR, defaults.barBackgroundColor)

            prefs[Keys.BAR_TEXT_COLOR] =
                getIntStrict(backup, Keys.BAR_TEXT_COLOR, defaults.barTextColor)

            prefs[Keys.SHOW_TIME] =
                getBooleanStrict(backup, Keys.SHOW_TIME, defaults.showTime)

            prefs[Keys.SHOW_DATE] =
                getBooleanStrict(backup, Keys.SHOW_DATE, defaults.showDate)

            prefs[Keys.TIME_FORMATTER] =
                getStringStrict(backup, Keys.TIME_FORMATTER, defaults.timeFormatter)

            prefs[Keys.DATE_FORMATTER] =
                getStringStrict(backup, Keys.DATE_FORMATTER, defaults.dateFormatter)

            prefs[Keys.SHOW_NOTIFICATIONS] =
                getBooleanStrict(backup, Keys.SHOW_NOTIFICATIONS, defaults.showNotifications)

            prefs[Keys.SHOW_BATTERY] =
                getBooleanStrict(backup, Keys.SHOW_BATTERY, defaults.showBattery)

            prefs[Keys.SHOW_CONNECTIVITY] =
                getBooleanStrict(backup, Keys.SHOW_CONNECTIVITY, defaults.showConnectivity)
        }
    }
}

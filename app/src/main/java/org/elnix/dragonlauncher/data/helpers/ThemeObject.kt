package org.elnix.dragonlauncher.data.helpers

import org.json.JSONObject

data class ThemeObject(
    val name: String,
    val json: JSONObject,
    val imageAssetPath: String?
)

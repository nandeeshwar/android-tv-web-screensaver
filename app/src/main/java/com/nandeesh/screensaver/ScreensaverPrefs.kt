package com.nandeesh.screensaver

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

class ScreensaverPrefs(context: Context) {

    private val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    val url: String
        get() = prefs.getString(KEY_URL, DEFAULT_URL) ?: DEFAULT_URL

    val javaScriptEnabled: Boolean
        get() = prefs.getBoolean(KEY_JAVASCRIPT, true)

    val interactive: Boolean
        get() = prefs.getBoolean(KEY_INTERACTIVE, false)

    companion object {
        const val KEY_URL = "screensaver_url"
        const val KEY_JAVASCRIPT = "enable_javascript"
        const val KEY_INTERACTIVE = "enable_interaction"
        const val DEFAULT_URL = "https://www.example.com"
    }
}

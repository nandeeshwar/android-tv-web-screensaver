package app.digiplex.screensaver

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import org.json.JSONArray

class ScreensaverPrefs(context: Context) {

    private val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    val activeUrl: String
        get() {
            val selected = prefs.getString(KEY_SELECTED_URL, null)
            if (selected != null && getUrls().contains(selected)) return selected
            return getUrls().firstOrNull() ?: DEFAULT_URL
        }

    val javaScriptEnabled: Boolean
        get() = prefs.getBoolean(KEY_JAVASCRIPT, true)

    val interactive: Boolean
        get() = prefs.getBoolean(KEY_INTERACTIVE, false)

    fun getUrls(): List<String> {
        val json = prefs.getString(KEY_URL_LIST, null) ?: return emptyList()
        return try {
            val array = JSONArray(json)
            (0 until array.length()).map { array.getString(it) }
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun addUrl(url: String) {
        val urls = getUrls().toMutableList()
        urls.remove(url)
        urls.add(0, url)
        saveUrls(urls)
        prefs.edit().putString(KEY_SELECTED_URL, url).apply()
    }

    fun removeUrl(url: String) {
        val urls = getUrls().toMutableList()
        urls.remove(url)
        saveUrls(urls)
        if (prefs.getString(KEY_SELECTED_URL, null) == url) {
            prefs.edit().putString(KEY_SELECTED_URL, urls.firstOrNull()).apply()
        }
    }

    fun setSelected(url: String) {
        prefs.edit().putString(KEY_SELECTED_URL, url).apply()
    }

    private fun saveUrls(urls: List<String>) {
        val array = JSONArray()
        urls.forEach { array.put(it) }
        prefs.edit().putString(KEY_URL_LIST, array.toString()).apply()
    }

    companion object {
        const val KEY_URL_LIST = "screensaver_url_list"
        const val KEY_SELECTED_URL = "screensaver_selected_url"
        const val KEY_JAVASCRIPT = "enable_javascript"
        const val KEY_INTERACTIVE = "enable_interaction"
        const val DEFAULT_URL = "https://www.example.com"
    }
}

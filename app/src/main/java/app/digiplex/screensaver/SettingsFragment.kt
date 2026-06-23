package app.digiplex.screensaver

import android.content.Intent
import android.os.Bundle
import androidx.leanback.preference.LeanbackPreferenceFragmentCompat
import androidx.preference.ListPreference
import androidx.preference.Preference

class SettingsFragment : LeanbackPreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.screensaver_prefs, rootKey)

        findPreference<Preference>("manage_urls")?.setOnPreferenceClickListener {
            startActivity(Intent(requireContext(), UrlListActivity::class.java))
            true
        }

        findPreference<ListPreference>(ScreensaverPrefs.KEY_SELECTED_URL)?.setOnPreferenceChangeListener { _, newValue ->
            ScreensaverPrefs(requireContext()).setSelected(newValue as String)
            refreshUrlPreferences()
            true
        }

    }

    override fun onResume() {
        super.onResume()
        refreshUrlPreferences()
    }

    private fun refreshUrlPreferences() {
        val prefs = ScreensaverPrefs(requireContext())
        val urls = prefs.getUrls()
        val count = urls.size

        findPreference<Preference>("manage_urls")?.summary = when (count) {
            0 -> "No URLs saved"
            1 -> "1 URL saved"
            else -> "$count URLs saved"
        }

        findPreference<ListPreference>(ScreensaverPrefs.KEY_SELECTED_URL)?.apply {
            if (urls.isEmpty()) {
                entries = arrayOf("No URLs saved")
                entryValues = arrayOf("")
                isEnabled = false
                summary = "Add URLs first"
            } else {
                entries = urls.toTypedArray()
                entryValues = urls.toTypedArray()
                isEnabled = true
                value = prefs.activeUrl
                summary = prefs.activeUrl
            }
        }
    }
}

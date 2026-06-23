package app.digiplex.screensaver

import android.content.Intent
import android.os.Bundle
import androidx.leanback.preference.LeanbackPreferenceFragmentCompat
import androidx.preference.Preference

class SettingsFragment : LeanbackPreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.screensaver_prefs, rootKey)

        findPreference<Preference>("manage_urls")?.setOnPreferenceClickListener {
            startActivity(Intent(requireContext(), UrlListActivity::class.java))
            true
        }
    }

    override fun onResume() {
        super.onResume()
        val prefs = ScreensaverPrefs(requireContext())
        val count = prefs.getUrls().size
        findPreference<Preference>("manage_urls")?.summary = when (count) {
            0 -> "No URLs saved"
            1 -> "1 URL saved — ${prefs.activeUrl}"
            else -> "$count URLs saved — active: ${prefs.activeUrl}"
        }
    }
}

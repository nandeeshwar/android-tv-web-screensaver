package com.nandeesh.screensaver

import android.os.Bundle
import androidx.leanback.preference.LeanbackPreferenceFragmentCompat

class SettingsFragment : LeanbackPreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.screensaver_prefs, rootKey)

        findPreference<androidx.preference.EditTextPreference>(ScreensaverPrefs.KEY_URL)?.apply {
            setSummaryProvider { pref ->
                val value = (pref as androidx.preference.EditTextPreference).text
                if (value.isNullOrBlank()) "Not set" else value
            }
        }
    }
}

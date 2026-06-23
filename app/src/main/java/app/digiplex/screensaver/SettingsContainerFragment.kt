package app.digiplex.screensaver

import androidx.fragment.app.Fragment
import androidx.leanback.preference.LeanbackPreferenceFragmentCompat
import androidx.leanback.preference.LeanbackSettingsFragmentCompat
import androidx.preference.Preference
import androidx.preference.PreferenceDialogFragmentCompat
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceScreen

class SettingsContainerFragment : LeanbackSettingsFragmentCompat() {

    override fun onPreferenceStartInitialScreen() {
        startPreferenceFragment(SettingsFragment())
    }

    override fun onPreferenceStartScreen(
        caller: PreferenceFragmentCompat,
        pref: PreferenceScreen
    ): Boolean {
        val fragment = SettingsFragment().apply {
            arguments = android.os.Bundle(1).apply {
                putString(LeanbackPreferenceFragmentCompat.ARG_PREFERENCE_ROOT, pref.key)
            }
        }
        startPreferenceFragment(fragment)
        return true
    }

    override fun onPreferenceStartFragment(
        caller: PreferenceFragmentCompat,
        pref: Preference
    ): Boolean {
        val fragment = childFragmentManager.fragmentFactory.instantiate(
            requireActivity().classLoader,
            pref.fragment ?: return false
        )
        fragment.setTargetFragment(caller, 0)
        if (fragment is PreferenceFragmentCompat || fragment is PreferenceDialogFragmentCompat) {
            startPreferenceFragment(fragment as Fragment)
        } else {
            startImmersiveFragment(fragment)
        }
        return true
    }
}

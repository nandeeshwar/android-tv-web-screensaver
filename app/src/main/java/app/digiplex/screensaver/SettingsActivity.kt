package app.digiplex.screensaver

import android.os.Bundle
import android.widget.TextView
import androidx.fragment.app.FragmentActivity

class SettingsActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        findViewById<TextView>(R.id.version_text)?.text =
            "v${packageManager.getPackageInfo(packageName, 0).versionName}"

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.settings_container, SettingsContainerFragment())
                .commit()
        }
    }
}

package app.digiplex.screensaver

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.FragmentActivity

class ScreensaverActivity : FragmentActivity() {

    private var webView: WebView? = null

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        setContentView(R.layout.dream_web)

        val prefs = ScreensaverPrefs(this)

        webView = findViewById<WebView>(R.id.webview).apply {
            webViewClient = WebViewClient()
            webChromeClient = WebChromeClient()
            settings.javaScriptEnabled = prefs.javaScriptEnabled
            settings.domStorageEnabled = true
            settings.mediaPlaybackRequiresUserGesture = false
            setLayerType(View.LAYER_TYPE_HARDWARE, null)
            loadUrl(prefs.activeUrl)
        }
    }

    override fun onDestroy() {
        webView?.apply {
            stopLoading()
            destroy()
        }
        webView = null
        super.onDestroy()
    }
}

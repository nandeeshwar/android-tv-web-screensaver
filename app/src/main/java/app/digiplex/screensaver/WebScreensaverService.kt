package app.digiplex.screensaver

import android.annotation.SuppressLint
import android.service.dreams.DreamService
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient

class WebScreensaverService : DreamService() {

    private var webView: WebView? = null

    @SuppressLint("SetJavaScriptEnabled")
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        val prefs = ScreensaverPrefs(this)

        isInteractive = prefs.interactive
        isFullscreen = true

        setContentView(R.layout.dream_web)

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

    override fun onDetachedFromWindow() {
        webView?.apply {
            stopLoading()
            destroy()
        }
        webView = null
        super.onDetachedFromWindow()
    }
}

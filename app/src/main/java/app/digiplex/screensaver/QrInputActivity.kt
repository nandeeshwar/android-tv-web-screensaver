package app.digiplex.screensaver

import android.net.wifi.WifiManager
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import java.net.Inet4Address
import java.net.NetworkInterface

class QrInputActivity : FragmentActivity() {

    private var server: InputServer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr_input)

        val ip = getDeviceIp()
        val statusText = findViewById<TextView>(R.id.status_text)
        val urlText = findViewById<TextView>(R.id.url_text)
        val qrImage = findViewById<ImageView>(R.id.qr_image)

        if (ip == null) {
            statusText.text = "No network connection found.\nConnect your TV to Wi-Fi and try again."
            return
        }

        val serverUrl = "http://$ip:${InputServer.PORT}"
        urlText.text = serverUrl

        qrImage.setImageBitmap(QrCodeGenerator.generate(serverUrl, 512))

        val prefs = ScreensaverPrefs(this)

        server = InputServer(InputServer.PORT) { url ->
            prefs.addUrl(url)
            runOnUiThread {
                statusText.text = "URL added: $url\nScan again to add more, or press Back."
            }
        }
        server?.start()
    }

    override fun onDestroy() {
        server?.stop()
        server = null
        super.onDestroy()
    }

    private fun getDeviceIp(): String? {
        try {
            val wifiManager = applicationContext.getSystemService(WIFI_SERVICE) as? WifiManager
            val wifiIp = wifiManager?.connectionInfo?.ipAddress
            if (wifiIp != null && wifiIp != 0) {
                return "%d.%d.%d.%d".format(
                    wifiIp and 0xff,
                    wifiIp shr 8 and 0xff,
                    wifiIp shr 16 and 0xff,
                    wifiIp shr 24 and 0xff
                )
            }
        } catch (_: Exception) {}

        try {
            for (iface in NetworkInterface.getNetworkInterfaces()) {
                for (addr in iface.inetAddresses) {
                    if (!addr.isLoopbackAddress && addr is Inet4Address) {
                        return addr.hostAddress
                    }
                }
            }
        } catch (_: Exception) {}

        return null
    }
}

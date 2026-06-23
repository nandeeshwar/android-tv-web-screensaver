package app.digiplex.screensaver

import android.graphics.Color
import android.net.wifi.WifiManager
import android.os.Bundle
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.net.Inet4Address
import java.net.NetworkInterface

class UrlListActivity : FragmentActivity() {

    private lateinit var prefs: ScreensaverPrefs
    private lateinit var adapter: UrlAdapter
    private lateinit var emptyView: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var urlInput: EditText
    private var server: InputServer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_url_list)

        prefs = ScreensaverPrefs(this)

        urlInput = findViewById(R.id.url_input)
        val addButton = findViewById<Button>(R.id.add_button)
        emptyView = findViewById(R.id.empty_view)
        recyclerView = findViewById(R.id.url_list)

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = UrlAdapter()
        recyclerView.adapter = adapter

        addButton.setOnClickListener { addUrlFromInput() }

        urlInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                addUrlFromInput()
                true
            } else false
        }

        setupQrCode()
    }

    override fun onResume() {
        super.onResume()
        refreshList()
    }

    override fun onDestroy() {
        server?.stop()
        server = null
        super.onDestroy()
    }

    private fun addUrlFromInput() {
        val raw = urlInput.text.toString().trim()
        if (raw.isEmpty()) return

        val finalUrl = if (!raw.startsWith("http://") && !raw.startsWith("https://")) {
            "https://$raw"
        } else raw

        if (!isValidUrl(finalUrl)) {
            Toast.makeText(this, "Invalid URL: $raw", Toast.LENGTH_SHORT).show()
            return
        }

        prefs.addUrl(finalUrl)
        urlInput.text.clear()
        refreshList()
        Toast.makeText(this, "Added: $finalUrl", Toast.LENGTH_SHORT).show()
    }

    private fun isValidUrl(url: String): Boolean {
        return try {
            val parsed = java.net.URL(url)
            parsed.protocol in listOf("http", "https") && parsed.host.contains(".")
        } catch (_: Exception) {
            false
        }
    }

    private fun setupQrCode() {
        val qrImage = findViewById<ImageView>(R.id.qr_image)
        val qrStatus = findViewById     <TextView>(R.id.qr_status)

        val ip = getDeviceIp()
        if (ip == null) {
            qrStatus.text = "No network"
            qrImage.visibility = View.GONE
            return
        }

        val serverUrl = "http://$ip:${InputServer.PORT}"
        qrStatus.text = serverUrl
        qrImage.setImageBitmap(QrCodeGenerator.generate(serverUrl, 512))

        server = InputServer(InputServer.PORT) { url ->
            prefs.addUrl(url)
            runOnUiThread {
                refreshList()
                Toast.makeText(this, "Added via phone: $url", Toast.LENGTH_SHORT).show()
            }
        }
        server?.start()
    }

    private fun refreshList() {
        val urls = prefs.getUrls()
        adapter.update(urls)
        emptyView.visibility = if (urls.isEmpty()) View.VISIBLE else View.GONE
        recyclerView.visibility = if (urls.isEmpty()) View.GONE else View.VISIBLE
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

    inner class UrlAdapter : RecyclerView.Adapter<UrlAdapter.ViewHolder>() {

        private var urls = listOf<String>()

        fun update(urls: List<String>) {
            this.urls = urls
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val row = LinearLayout(parent.context).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                setPadding(24, 20, 24, 20)
                isFocusable = true
                isFocusableInTouchMode = true
                setBackgroundColor(Color.parseColor("#1A1A1A"))
                layoutParams = RecyclerView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply { bottomMargin = 4 }
            }

            val urlText = TextView(parent.context).apply {
                setTextColor(Color.parseColor("#EEEEEE"))
                textSize = 18f
                layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
            }

            val deleteBtn = TextView(parent.context).apply {
                text = "DELETE"
                textSize = 14f
                setTextColor(Color.parseColor("#FF6666"))
                setPadding(24, 12, 24, 12)
                setBackgroundColor(Color.parseColor("#2A1A1A"))
                isFocusable = true
                isFocusableInTouchMode = true
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                setOnFocusChangeListener { v, hasFocus ->
                    (v as TextView).setBackgroundColor(
                        if (hasFocus) Color.parseColor("#CC3333") else Color.parseColor("#2A1A1A")
                    )
                    v.setTextColor(
                        if (hasFocus) Color.WHITE else Color.parseColor("#FF6666")
                    )
                }
            }

            row.addView(urlText)
            row.addView(deleteBtn)

            return ViewHolder(row, urlText, deleteBtn)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val url = urls[position]

            holder.urlText.text = url

            holder.row.setOnFocusChangeListener { v, hasFocus ->
                v.setBackgroundColor(
                    if (hasFocus) Color.parseColor("#2A2A2A") else Color.parseColor("#1A1A1A")
                )
            }

            holder.deleteBtn.setOnClickListener {
                prefs.removeUrl(url)
                refreshList()
            }
            holder.deleteBtn.setOnKeyListener { v, keyCode, event ->
                if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
                    v.performClick()
                    true
                } else false
            }
        }

        override fun getItemCount() = urls.size

        inner class ViewHolder(
            val row: LinearLayout,
            val urlText: TextView,
            val deleteBtn: TextView
        ) : RecyclerView.ViewHolder(row)
    }
}

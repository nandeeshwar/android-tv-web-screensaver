package app.digiplex.screensaver

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class UrlListActivity : FragmentActivity() {

    private lateinit var prefs: ScreensaverPrefs
    private lateinit var adapter: UrlAdapter
    private lateinit var emptyView: TextView
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        prefs = ScreensaverPrefs(this)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#111111"))
            setPadding(48, 48, 48, 48)
        }

        val header = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = 32 }
        }

        header.addView(TextView(this).apply {
            text = "Saved URLs"
            setTextColor(Color.WHITE)
            textSize = 28f
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
        })

        header.addView(TextView(this).apply {
            text = "[+] Add via QR"
            setTextColor(Color.parseColor("#8888FF"))
            textSize = 18f
            isFocusable = true
            isFocusableInTouchMode = true
            setPadding(24, 12, 24, 12)
            setBackgroundColor(Color.parseColor("#222222"))
            setOnClickListener {
                startActivity(Intent(this@UrlListActivity, QrInputActivity::class.java))
            }
            setOnKeyListener { _, keyCode, event ->
                if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
                    performClick()
                    true
                } else false
            }
            setOnFocusChangeListener { v, hasFocus ->
                (v as TextView).setBackgroundColor(
                    if (hasFocus) Color.parseColor("#333366") else Color.parseColor("#222222")
                )
            }
        })

        root.addView(header)

        emptyView = TextView(this).apply {
            text = "No URLs saved yet.\nAdd one using the QR button above."
            setTextColor(Color.parseColor("#AAAAAA"))
            textSize = 18f
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            visibility = View.GONE
        }

        recyclerView = RecyclerView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            layoutManager = LinearLayoutManager(this@UrlListActivity)
        }

        adapter = UrlAdapter()
        recyclerView.adapter = adapter

        root.addView(recyclerView)
        root.addView(emptyView)

        setContentView(root)
    }

    override fun onResume() {
        super.onResume()
        refreshList()
    }

    private fun refreshList() {
        val urls = prefs.getUrls()
        adapter.update(urls, prefs.activeUrl)
        emptyView.visibility = if (urls.isEmpty()) View.VISIBLE else View.GONE
        recyclerView.visibility = if (urls.isEmpty()) View.GONE else View.VISIBLE
    }

    inner class UrlAdapter : RecyclerView.Adapter<UrlAdapter.ViewHolder>() {

        private var urls = listOf<String>()
        private var selectedUrl = ""

        fun update(urls: List<String>, selected: String) {
            this.urls = urls
            this.selectedUrl = selected
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

            val indicator = View(parent.context).apply {
                layoutParams = LinearLayout.LayoutParams(8, 48).apply { rightMargin = 16 }
            }

            val urlText = TextView(parent.context).apply {
                setTextColor(Color.parseColor("#EEEEEE"))
                textSize = 18f
                layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
            }

            val deleteBtn = ImageView(parent.context).apply {
                setImageResource(android.R.drawable.ic_menu_close_clear_cancel)
                setPadding(16, 16, 16, 16)
                isFocusable = true
                isFocusableInTouchMode = true
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                setOnFocusChangeListener { v, hasFocus ->
                    v.alpha = if (hasFocus) 1.0f else 0.5f
                }
                alpha = 0.5f
            }

            row.addView(indicator)
            row.addView(urlText)
            row.addView(deleteBtn)

            return ViewHolder(row, indicator, urlText, deleteBtn)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val url = urls[position]
            val isSelected = url == selectedUrl

            holder.urlText.text = url
            holder.urlText.setTypeface(null, if (isSelected) Typeface.BOLD else Typeface.NORMAL)
            holder.indicator.setBackgroundColor(
                if (isSelected) Color.parseColor("#44CC44") else Color.TRANSPARENT
            )

            holder.row.setOnClickListener {
                prefs.setSelected(url)
                refreshList()
            }
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
            val indicator: View,
            val urlText: TextView,
            val deleteBtn: ImageView
        ) : RecyclerView.ViewHolder(row)
    }
}

package com.nicoqueijo.android.currencyconverter.kotlin.view

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.nicoqueijo.android.currencyconverter.R

class SourceCodeFragment_kt : Fragment(), ViewTreeObserver.OnScrollChangedListener {

    private lateinit var toobar: Toolbar
    private lateinit var webView: WebView
    private lateinit var progressBar: ProgressBar
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_source_code_kt, container, false)
        initViews(view)
        setUpWebClientsAndSwipeLayout()
        handleBackPress()
        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_open_in_browser_kt, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val sourceCodeIntent = Intent(Intent.ACTION_VIEW)
        sourceCodeIntent.data = Uri.parse("https://github.com/nicoqueijo/Android-Currency-Converter")
        val sourceCodeChooser = Intent.createChooser(sourceCodeIntent,
                getString(R.string.launch_browser))
        startActivity(sourceCodeChooser)
        return super.onOptionsItemSelected(item)
    }

    // This might not be needed. Test to confirm.
//    override fun onDestroyView() {
//        super.onDestroyView()
//        toobar.menu.removeItem(R.id.open_in_browser)
//    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initViews(view: View) {
        webView = view.findViewById(R.id.web_view_kt)
        progressBar = view.findViewById(R.id.progress_bar_kt)
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout_kt)
        val webViewSettings: WebSettings = webView.settings
        webViewSettings.javaScriptEnabled = true
        webViewSettings.builtInZoomControls = true
        webViewSettings.displayZoomControls = false
        webView.loadUrl("https://github.com/nicoqueijo/Android-Currency-Converter")
        webView.viewTreeObserver.addOnScrollChangedListener(this)
    }

    private fun setUpWebClientsAndSwipeLayout() {
        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                progressBar.visibility = View.VISIBLE
            }

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                progressBar.visibility = View.GONE
                swipeRefreshLayout.isRefreshing = false
            }
        }
        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                progressBar.progress = newProgress
            }
        }
        swipeRefreshLayout.setOnRefreshListener {
            webView.reload()
        }
    }

    private fun handleBackPress() {
        webView.setOnKeyListener { _, keyCode: Int, event: KeyEvent ->
            val hostingActivity = activity as MainActivity_kt
            if (hostingActivity.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                hostingActivity.drawerLayout.closeDrawer(GravityCompat.START)
                return@setOnKeyListener true
            }
            if (keyCode == KeyEvent.KEYCODE_BACK
                    && event.action == MotionEvent.ACTION_UP
                    && webView.canGoBack()) {
                webView.goBack()
                return@setOnKeyListener true
            }
            false
        }
    }

    override fun onScrollChanged() {
        swipeRefreshLayout.isEnabled = webView.scrollY == 0
    }
}

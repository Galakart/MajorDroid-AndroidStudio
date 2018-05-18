package ru.galakart.majordroid


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.view.*
import android.webkit.*
import android.widget.ProgressBar
import android.widget.TableLayout
import android.widget.Toast


class MainActivity : Activity() {

    private var mWebView: WebView? = null
    private var webPost: WebView? = null
    private var Pbar: ProgressBar? = null
    private var localURL: String = ""
    private var globalURL: String = ""
    private var serverURL = ""
    private var login: String? = ""
    private var passw: String? = ""
    private var pathHomepage: String? = ""
    private var pathVoice: String? = ""
    private var tmpDostupAccess = ""
    private var tmpAdressAccess = ""
    private var outAccess = false
    private var firstLoad = false

    override fun onPause() {
        super.onPause()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Pbar = findViewById<View>(R.id.pB1) as ProgressBar
        mWebView = findViewById<View>(R.id.webview) as WebView
        mWebView!!.settings.javaScriptEnabled = true
        mWebView!!.settings.cacheMode = WebSettings.LOAD_NO_CACHE
        mWebView!!.webViewClient = MajorDroidWebViewer()
        webPost = findViewById<View>(R.id.webPost) as WebView

        mWebView!!.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, progress: Int) {
                if (progress < 100 && Pbar!!.visibility == ProgressBar.INVISIBLE) {
                    Pbar!!.visibility = ProgressBar.VISIBLE
                }
                Pbar!!.progress = progress
                if (progress == 100) {
                    Pbar!!.visibility = ProgressBar.INVISIBLE
                }
            }
        }

        val handler = Handler()
        val prefs = PreferenceManager
                .getDefaultSharedPreferences(this)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && mWebView!!.canGoBack()) {
            mWebView!!.goBack()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            R.id.action_about -> {
                val ab = Intent(this, AboutActivity::class.java)
                startActivity(ab)
                return true
            }

            R.id.action_quit -> {

                finish()
                return true
            }

            R.id.action_settings -> {
                val st = Intent(this, PrefsActivity::class.java)
                startActivity(st)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    inner class MajorDroidWebViewer : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            view.loadUrl(url)
            return true
        }

        override fun onReceivedHttpAuthRequest(view: WebView,
                                               handler: HttpAuthHandler, host: String, realm: String) {
            if (outAccess)
                handler.proceed(login, passw)
        }
    }

    public override fun onResume() {
        super.onResume()
        loadHomePage(0)
    }

    private fun loadHomePage(immediateLoad: Int) {
        val prefs = PreferenceManager
                .getDefaultSharedPreferences(this)
        localURL = prefs.getString(getString(R.string.localaddress), "")
        globalURL = prefs.getString(getString(R.string.globaladdress), "")
        pathHomepage = prefs.getString(getString(R.string.homepage_default), "")
        pathVoice = prefs.getString(getString(R.string.voiceprocessor_default), "")
        login = prefs.getString(getString(R.string.login), "")
        passw = prefs.getString(getString(R.string.passw), "")
        val dostup = prefs.getString(getString(R.string.access), "")
        val vid = prefs.getString(getString(R.string.view), "")
        val wifiHomeNet = prefs.getString("wifihomenet", "")
        var wifiToast = ""
        val tl = findViewById<View>(R.id.homeTableLay) as TableLayout

        if (vid!!.contains("Обычный")) {
            window.addFlags(
                    WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            tl.visibility = View.VISIBLE
        }

        if (vid.contains("Полноэкранный")) {
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            window.clearFlags(
                    WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
            tl.visibility = View.VISIBLE
        }

        if (vid.contains("Полноэкранный (без панели кнопок)")) {
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            window.clearFlags(
                    WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
            tl.visibility = View.GONE
        }

        if (dostup != tmpDostupAccess)
            firstLoad = false

        if (dostup!!.contains("Локальный")) {
            outAccess = false
            serverURL = localURL
            wifiToast = ""
            tmpDostupAccess = dostup

        } else if (dostup.contains("Глобальный")) {
            outAccess = true
            serverURL = globalURL
            wifiToast = ""
            tmpDostupAccess = dostup

        } else if (dostup.contains("Автоматический")) {
            if (wifiHomeNet !== "") {
                if (isConnectedToSSID(wifiHomeNet)) {
                    outAccess = false
                    serverURL = localURL
                    wifiToast = " (SSID: $wifiHomeNet)"
                } else {
                    outAccess = true
                    serverURL = globalURL
                    wifiToast = " (не в домашней сети)"
                }
            } else {
                outAccess = false
                serverURL = localURL
                wifiToast = " (не задана домашняя wifi-сеть)"
            }
            tmpDostupAccess = dostup
        }

        if (serverURL != tmpAdressAccess)
            firstLoad = false

        if (!firstLoad || immediateLoad == 1) {
            val toast = Toast.makeText(applicationContext, "",
                    Toast.LENGTH_LONG)
            toast.setGravity(Gravity.BOTTOM, 0, 0)
            if (outAccess)
                toast.setText("Глобальный доступ$wifiToast")
            else
                toast.setText("Локальный доступ$wifiToast")
            if (serverURL === "") {
                toast.setText("Не задан адрес сервера в настройках")
                toast.show()
            } else {
                mWebView!!.loadUrl("http://$serverURL$pathHomepage")

                // потом использовать reload();

                firstLoad = true
                if (serverURL != tmpAdressAccess)
                    toast.show()
                tmpAdressAccess = serverURL
            }
        }
    }

    private fun voiceCommand(command: String) {
        webPost!!.loadUrl("http://$serverURL$pathVoice$command")
        val toast = Toast.makeText(applicationContext, command,
                Toast.LENGTH_LONG)
        toast.setGravity(Gravity.BOTTOM, 0, 0)
        toast.show()
    }

    fun imgb_home_click(v: View?) {
        loadHomePage(1)
    }

    fun imgb_voice_click(v: View?) {

    }

    fun imgb_pult_click(v: View?) {
        val j = Intent(this, ControsActivity::class.java)
        startActivity(j)
    }

    fun imgb_settings_click(v: View?) {
        val i = Intent(this, PrefsActivity::class.java)
        startActivity(i)
    }

    internal fun isConnectedToSSID(t: String): Boolean {
        try {
            val wifiMgr = this
                    .applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val wifiInfo = wifiMgr.connectionInfo
            if (wifiInfo.ssid == t)
                return true
        } catch (a: Exception) {
        }
        return false
    }
}
/*
 * На будущее: 1. Использовать reload(); при обновлении браузера 2. Использовать
 * окно браузера для вывода возможных ошибок, вот так String summary =
 * "<html><body>You scored <b>192</b> points.</body></html>";
 * webview.loadData(summary, "text/html", null);
 */
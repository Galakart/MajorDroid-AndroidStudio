package ru.galakart.majordroid

import android.app.Activity
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.method.ScrollingMovementMethod
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.webkit.HttpAuthHandler
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import android.widget.Toast

class ControsActivity : Activity() {
    private var webPost: WebView? = null
    private var serverURL: String? = null
    private var login: String? = null
    private var passw: String? = null
    private var pathScripts: String? = ""
    private var outAccess = false
    private var prefs: SharedPreferences? = null
    private var textView_legend: TextView? = null
    private val scriptnames = arrayOfNulls<String>(9)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefs = PreferenceManager
                .getDefaultSharedPreferences(this)
        val vid = prefs.getString("view", "")
        pathScripts = prefs.getString("scriptprocessor_default", "")
        if (vid!!.contains("1")) {
            window.addFlags(
                    WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
        if (vid.contains("2")) {
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            window.clearFlags(
                    WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
        }
        setContentView(R.layout.activity_contros)
        webPost = findViewById<View>(R.id.webPost) as WebView
        webPost!!.settings.javaScriptEnabled = true
        webPost!!.webViewClient = MajorDroidWebViewer()
        textView_legend = findViewById<View>(R.id.textView_legend) as TextView
        textView_legend!!.movementMethod = ScrollingMovementMethod()
    }

    private inner class MajorDroidWebViewer : WebViewClient() {
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
        prefs = PreferenceManager.getDefaultSharedPreferences(this)

        val localURL = prefs!!.getString("localaddress", "")
        val globalURL = prefs!!.getString("globaladdress", "")
        val dostup = prefs!!.getString("access", "")
        login = prefs!!.getString("login", "")
        passw = prefs!!.getString("passw", "")
        scriptnames[0] = prefs!!.getString("scriptname1", "")
        scriptnames[1] = prefs!!.getString("scriptname2", "")
        scriptnames[2] = prefs!!.getString("scriptname3", "")
        scriptnames[3] = prefs!!.getString("scriptname4", "")
        scriptnames[4] = prefs!!.getString("scriptname5", "")
        scriptnames[5] = prefs!!.getString("scriptname6", "")
        scriptnames[6] = prefs!!.getString("scriptname7", "")
        scriptnames[7] = prefs!!.getString("scriptname8", "")
        scriptnames[8] = prefs!!.getString("scriptname9", "")
        if (dostup!!.contains("1")) {
            outAccess = false
            serverURL = localURL
        } else if (dostup.contains("2")) {
            outAccess = true
            serverURL = globalURL
        }
        var legend = ""
        for (i in 0..8) {
            if (scriptnames[i] !== "")
                legend += (i + 1).toString() + ". " + scriptnames[i] + ".\n"
            else
                legend += (i + 1).toString() + ". Скрипт не задан.\n"
        }
        textView_legend!!.text = legend
    }

    fun imgb_script1_exec(v: View) {
        val script = scriptnames[0]
        if (script !== "")
            scriptExec(script)
        else
            scriptneni()
    }

    fun imgb_script2_exec(v: View) {
        val script = scriptnames[1]
        if (script !== "")
            scriptExec(script)
        else
            scriptneni()
    }

    fun imgb_script3_exec(v: View) {
        val script = scriptnames[2]
        if (script !== "")
            scriptExec(script)
        else
            scriptneni()
    }

    fun imgb_script4_exec(v: View) {
        val script = scriptnames[3]
        if (script !== "")
            scriptExec(script)
        else
            scriptneni()
    }

    fun imgb_script5_exec(v: View) {
        val script = scriptnames[4]
        if (script !== "")
            scriptExec(script)
        else
            scriptneni()
    }

    fun imgb_script6_exec(v: View) {
        val script = scriptnames[5]
        if (script !== "")
            scriptExec(script)
        else
            scriptneni()
    }

    fun imgb_script7_exec(v: View) {
        val script = scriptnames[6]
        if (script !== "")
            scriptExec(script)
        else
            scriptneni()
    }

    fun imgb_script8_exec(v: View) {
        val script = scriptnames[7]
        if (script !== "")
            scriptExec(script)
        else
            scriptneni()
    }

    fun imgb_script9_exec(v: View) {
        val script = scriptnames[8]
        if (script !== "")
            scriptExec(script)
        else
            scriptneni()
    }

    fun scriptExec(script: String?) {
        webPost!!.loadUrl("http://$serverURL$pathScripts$script")
        val toast = Toast.makeText(applicationContext, "Скрипт $script", Toast.LENGTH_LONG)
        toast.setGravity(Gravity.BOTTOM, 0, 0)
        toast.show()
    }

    private fun scriptneni() {
        val toast = Toast
                .makeText(
                        applicationContext,
                        "Кнопка не привязана к скрипту.\nЗадайте имя скрипта в настройках",
                        Toast.LENGTH_LONG)
        toast.setGravity(Gravity.BOTTOM, 0, 0)
        toast.show()
    }
}

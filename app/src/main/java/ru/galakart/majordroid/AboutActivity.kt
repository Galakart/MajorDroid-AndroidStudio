package ru.galakart.majordroid

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import android.view.WindowManager

class AboutActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = PreferenceManager
                .getDefaultSharedPreferences(this)
        val vid = prefs.getString(getString(R.string.view), "")
        if (vid!!.contains("Обычный")) {
            window.addFlags(
                    WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }

        if (vid.contains("Полноэкранный")) {
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            window.clearFlags(
                    WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
        }

        setContentView(R.layout.activity_about)
    }

    fun btn_rate_exec(v: View) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse("market://details?id=ru.galakart.majordroid")
        startActivity(intent)
    }

}

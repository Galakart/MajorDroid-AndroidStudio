package ru.galakart.majordroid

import android.app.AlertDialog
import android.content.Context
import android.net.wifi.WifiManager
import android.os.Bundle
import android.preference.Preference
import android.preference.PreferenceActivity
import android.preference.PreferenceManager
import android.view.Gravity
import android.view.WindowManager
import android.widget.Toast

class PrefsActivity : PreferenceActivity() {
    private var currentSSID: String? = null
    private var tmpPrefSSID: String? = null
    internal lateinit var ad: AlertDialog.Builder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefs = PreferenceManager
                .getDefaultSharedPreferences(this)
        val vid = prefs.getString("view", "")

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
        addPreferencesFromResource(R.xml.settings)

        val wifiMgr = this
                .applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiInfo = wifiMgr.connectionInfo
        currentSSID = wifiInfo.ssid
        tmpPrefSSID = prefs.getString("wifihomenet", "")

        val button = findPreference("buttonWifiHomeNet") as Preference
        button.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            ad = AlertDialog.Builder(this@PrefsActivity)
            ad.setTitle("Выбор домашней Wifi-сети") // заголовок
            if (tmpPrefSSID === "")
                ad.setMessage("Установить домашнюю сеть на " + currentSSID
                        + " ?")
            else
                ad.setMessage("Текущая домашняя сеть: " + tmpPrefSSID
                        + "\nПоменять её на " + currentSSID + " ?")
            ad.setPositiveButton("Сохранить") { dialog, arg1 ->
                val toast = Toast.makeText(applicationContext,
                        "Сеть $currentSSID сохранена",
                        Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.BOTTOM, 0, 0)
                val editor = prefs.edit()
                editor.putString("wifihomenet", currentSSID)
                if (editor.commit())
                    toast.show()
            }
            ad.setNegativeButton("Отмена") { dialog, arg1 ->
                // none
            }
            ad.setCancelable(true)

            if (currentSSID != null) {
                if (tmpPrefSSID == currentSSID) {

                    val albuilder = AlertDialog.Builder(
                            this@PrefsActivity)
                    albuilder
                            .setTitle("Сообщение")
                            .setMessage(
                                    "Текущая WiFi-сеть совпадает с занесённой в память. Для записи другой домашней сети, подключитесь к ней.")
                            .setCancelable(false)
                            .setNegativeButton("Назад"
                            ) { dialog, id -> dialog.cancel() }
                    val alert = albuilder.create()
                    alert.show()

                } else
                    ad.show()
            } else {
                val albuilder = AlertDialog.Builder(
                        this@PrefsActivity)
                albuilder
                        .setTitle("Важное сообщение!")
                        .setMessage("WiFi выключен или нет соединения")
                        .setCancelable(false)
                        .setNegativeButton("Назад"
                        ) { dialog, id -> dialog.cancel() }
                val alert = albuilder.create()
                alert.show()
            }

            true
        }

        //		Preference button_path_default = (Preference) findPreference("button_path_default");
        //		button_path_default.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
        //			@Override
        //			public boolean onPreferenceClick(Preference arg0) {
        //				Editor editor = prefs.edit();
        //				editor.putString("path_homepage", "/menu.html");
        //				editor.putString("path_scripts", "/objects/?script=");
        //				editor.putString("path_voice", "/command.php?qry=");
        //				editor.putString("path_gps", "/gps.php");
        //				Toast toast = Toast.makeText(getApplicationContext(),
        //						"Умолчания восстановлены",
        //						Toast.LENGTH_SHORT);
        //				toast.setGravity(Gravity.BOTTOM, 0, 0);
        //				if (editor.commit())
        //					toast.show();
        //				return true;
        //			}
        //		});

    }
}

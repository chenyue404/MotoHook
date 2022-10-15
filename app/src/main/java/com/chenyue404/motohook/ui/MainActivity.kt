package com.chenyue404.motohook.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.FragmentActivity
import com.chenyue404.motohook.BuildConfig
import com.chenyue404.motohook.R

/**
 * Created by chenyue on 2022/9/18 0018.
 */
class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction()
            .replace(R.id.llRoot, SettingsFragment())
            .commit()

        val pref = try {
            getSharedPreferences(
                "${BuildConfig.APPLICATION_ID}_preferences",
                Context.MODE_WORLD_READABLE
            )
        } catch (e: SecurityException) {
            log("The new XSharedPreferences is not enabled or module's not loading")
            // The new XSharedPreferences is not enabled or module's not loading
            null // other fallback, if any
        }
        val value_settings_hide_header_suggestion =
            pref?.getBoolean(getString(R.string.key_settings_hide_header_suggestion), false)
        log("value_settings_hide_header_suggestion=$value_settings_hide_header_suggestion")
    }

    private fun log(str: String) {
        Log.d("Moto-hook-assistant-", str)
    }

}
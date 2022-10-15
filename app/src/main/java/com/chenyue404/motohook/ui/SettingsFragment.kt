package com.chenyue404.motohook.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.chenyue404.motohook.R

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        log(preferenceManager.sharedPreferencesName)
    }

    override fun getPreferenceManager(): PreferenceManager {
        return super.getPreferenceManager().apply {
            sharedPreferencesMode = Context.MODE_WORLD_READABLE
        }
    }

    private fun log(str: String) {
        Log.d("Moto-hook-assistant-", str)
    }
}
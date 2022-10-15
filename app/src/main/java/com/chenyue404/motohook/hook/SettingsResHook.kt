package com.chenyue404.motohook.hook

import com.chenyue404.motohook.PluginEntry
import de.robv.android.xposed.IXposedHookInitPackageResources
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.callbacks.XC_InitPackageResources

/**
 * Created by chenyue on 2022/9/18 0018.
 */
class SettingsResHook : IXposedHookInitPackageResources {

    private val PACKAGE_NAME = "com.android.settings"
    private val TAG = "Moto-hook-settings-"

    override fun handleInitPackageResources(resparam: XC_InitPackageResources.InitPackageResourcesParam) {
        val packageName = resparam.packageName

        if (packageName != PACKAGE_NAME) {
            return
        }

        log("")

        // 去除设置头部的建议
        if (PluginEntry.pref?.getBoolean("key_settings_hide_header_suggestion", false) == true) {
            resparam.res.setReplacement(packageName, "bool", "config_use_legacy_suggestion", false)
        }
    }

    private fun log(str: String) {
        XposedBridge.log("$TAG$str")
    }

}
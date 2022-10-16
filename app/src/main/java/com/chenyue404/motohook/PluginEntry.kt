package com.chenyue404.motohook

import android.content.SharedPreferences
import com.chenyue404.motohook.hook.AssistantHook
import com.chenyue404.motohook.hook.LauncherHook
import com.chenyue404.motohook.hook.SystemUIHook
import com.chenyue404.motohook.hook.VoiceSTHook
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XSharedPreferences
import de.robv.android.xposed.callbacks.XC_LoadPackage

/**
 * Created by chenyue on 2022/9/18 0018.
 */
class PluginEntry : IXposedHookLoadPackage {
    companion object {
        val pref: SharedPreferences? by lazy {
            val pref = XSharedPreferences(
                BuildConfig.APPLICATION_ID,
                "${BuildConfig.APPLICATION_ID}_preferences"
            )
            if (pref.file.canRead()) pref else null
        }
    }

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        listOf(
            SystemUIHook(),
//            FreeFormHook(),
            LauncherHook(),
            AssistantHook(),
            VoiceSTHook(),
        ).forEach {
            it.handleLoadPackage(lpparam)
        }
    }
}
package com.chenyue404.motohook

import com.chenyue404.motohook.hook.LauncherHook
import com.chenyue404.motohook.hook.SystemUIHook
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.callbacks.XC_LoadPackage

/**
 * Created by chenyue on 2022/9/18 0018.
 */
class PluginEntry : IXposedHookLoadPackage {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        listOf(
            SystemUIHook(),
//            FreeFormHook(),
            LauncherHook(),
        ).forEach {
            it.handleLoadPackage(lpparam)
        }
    }
}
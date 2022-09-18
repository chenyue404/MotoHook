package com.chenyue404.motohook.hook

import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.callbacks.XC_LoadPackage

/**
 * Created by chenyue on 2022/9/18 0018.
 */
class PluginEntry : IXposedHookLoadPackage {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        listOf(
            SystemUIHook()
        ).forEach {
            it.handleLoadPackage(lpparam)
        }
    }
}
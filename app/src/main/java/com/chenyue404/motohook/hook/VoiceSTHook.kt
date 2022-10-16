package com.chenyue404.motohook.hook

import com.chenyue404.motohook.PluginEntry
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers.findAndHookMethod
import de.robv.android.xposed.callbacks.XC_LoadPackage

/**
 * Created by chenyue on 2022/9/18 0018.
 */
class VoiceSTHook : IXposedHookLoadPackage {

    private val PACKAGE_NAME = "com.lenovo.levoice.trigger"
    private val TAG = "Moto-hook-VoiceST-"

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {

        val packageName = lpparam.packageName
        val classLoader = lpparam.classLoader

        if (packageName != PACKAGE_NAME) {
            return
        }

        log("")

        if (PluginEntry.pref?.getBoolean("key_leVoiceST_can_weak_up_by_le", false) == true) {
            findAndHookMethod("com.lenovo.levoice.trigger.soundmodel.SoundModelUtils",
                classLoader,
                "canUseXiaole",
                object : XC_MethodHook() {
                    override fun beforeHookedMethod(param: MethodHookParam) {
                        log("canUseXiaole")
                        param.result = true
                    }
                })
        }
    }

    private fun log(str: String) {
        XposedBridge.log("$TAG$str")
    }
}
package com.chenyue404.motohook.hook

import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers.findAndHookMethod
import de.robv.android.xposed.callbacks.XC_LoadPackage

/**
 * Created by chenyue on 2022/9/18 0018.
 */
class SystemUIHook : IXposedHookLoadPackage {

    private val PACKAGE_NAME = "com.motorola.freeform"
    private val TAG = "Moto-freeform-hook-"

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {

        val packageName = lpparam.packageName
        val classLoader = lpparam.classLoader

        if (packageName != PACKAGE_NAME) {
            return
        }

        log("")

        findAndHookMethod(
            "b.d.a.j.b", classLoader,
            "c",
            String::class.java,
            object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam) {
                    log("b.d.a.j.b#c")
                    val str = param.args[0] as String
                    log("str=$str")
                }
            }
        )
    }

    private fun log(str: String) {
        XposedBridge.log("$TAG$str")
    }
}
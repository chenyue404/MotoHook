package com.chenyue404.motohook.hook

import android.graphics.Color
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers.findAndHookMethod
import de.robv.android.xposed.callbacks.XC_LoadPackage

/**
 * Created by chenyue on 2022/9/18 0018.
 */
class LauncherHook : IXposedHookLoadPackage {

    private val PACKAGE_NAME = "com.motorola.launcher3"
    private val TAG = "Moto-hook-launcher-"

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {

        val packageName = lpparam.packageName
        val classLoader = lpparam.classLoader

        if (packageName != PACKAGE_NAME) {
            return
        }

        log("")

        // 最近任务背景设置透明
        findAndHookMethod(
            "com.android.launcher3.views.ScrimView", classLoader,
            "setBackgroundColor",
            Int::class.java,
            object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam) {
//                    log("com.android.launcher3.views.ScrimView#setBackgroundColor")
//                    val colorInt = param.args[0] as Int
//                    val colorStr = String.format("#%06X", (0xFFFFFF and colorInt))
//                    log("colorStr=$colorStr")
                    param.args[0] = Color.TRANSPARENT
                }
            }
        )
    }

    private fun log(str: String) {
        XposedBridge.log("$TAG$str")
    }
}
package com.chenyue404.motohook.hook

import android.app.AndroidAppHelper
import android.content.ComponentName
import android.content.Intent
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.view.View
import com.chenyue404.motohook.PluginEntry
import de.robv.android.xposed.*
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
        if (PluginEntry.pref?.getBoolean(
                "key_Launcher_recent_tasks_transparent_bg", false
            ) == true
        ) {
            findAndHookMethod("com.android.launcher3.views.ScrimView",
                classLoader,
                "setBackgroundColor",
                Int::class.java,
                object : XC_MethodHook() {
                    override fun beforeHookedMethod(param: MethodHookParam) {
//                        log("com.android.launcher3.views.ScrimView#setBackgroundColor")
//                    val colorInt = param.args[0] as Int
//                    val colorStr = String.format("#%06X", (0xFFFFFF and colorInt))
//                    log("colorStr=$colorStr")
                        param.args[0] = Color.TRANSPARENT
                    }
                })
        }

        if (PluginEntry.pref?.getBoolean(
                "key_Launcher_show_share_screen_shot_btn", false
            ) == true
        ) {
            findAndHookMethod("com.android.launcher3.config.FeatureFlags.BooleanFlag",
                classLoader,
                "get",
                object : XC_MethodHook() {
                    override fun beforeHookedMethod(param: MethodHookParam) {
//                    log("com.android.launcher3.config.FeatureFlags.BooleanFlag#get")
                        val key = XposedHelpers.findField(param.thisObject.javaClass, "key")
                            .get(param.thisObject) as String
//                    val value = XposedHelpers.getBooleanField(param.thisObject, "key")
//                    log("key=$key, value=$value")
                        if (key == "ENABLE_OVERVIEW_SHARE") {
                            param.result = true
                        }
                    }
                })
        }

        if (PluginEntry.pref?.getBoolean("key_Launcher_long_press_open_freeform", false) == true) {
            findAndHookMethod(
                "com.android.quickstep.views.TaskView", classLoader,
                "lambda\$setIcon\$6\$TaskView",
                View::class.java,
                object : XC_MethodReplacement() {
                    override fun replaceHookedMethod(param: MethodHookParam): Any {
                        log("com.android.quickstep.views.TaskView#lambda\$setIcon\$6\$TaskView")

                        val workspaceItemInfo =
                            XposedHelpers.callMethod(param.thisObject, "getItemInfo")
                        val componentName = XposedHelpers.callMethod(
                            workspaceItemInfo,
                            "getTargetComponent"
                        ) as ComponentName
                        val pkg = componentName.packageName

                        log("pkg=$pkg")

                        val recentsView =
                            XposedHelpers.callMethod(param.thisObject, "getRecentsView")
                        XposedHelpers.callMethod(recentsView, "finishRecentsAnimation", false, null)

                        Handler(Looper.getMainLooper()).postDelayed({
                            AndroidAppHelper.currentApplication().sendBroadcast(
                                Intent("com.chenyue404.motohook.freeform")
                                    .putExtra("package", pkg)
                            )
                        }, 300)

                        return true
                    }
                }
            )
        }
    }

    private fun log(str: String) {
        XposedBridge.log("$TAG$str")
    }
}
package com.chenyue404.motohook.hook

import android.app.AndroidAppHelper
import android.content.ComponentName
import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.view.View
import android.widget.Button
import com.chenyue404.motohook.PluginEntry
import de.robv.android.xposed.*
import de.robv.android.xposed.XposedHelpers.*
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

        if (!PluginEntry.isAndroid13 &&
            PluginEntry.pref?.getBoolean(
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

            val showAsBubble = PluginEntry.isAndroid13 &&
                    PluginEntry.pref?.getBoolean(
                        "key_Launcher_long_press_open_freeform_as_bubble",
                        false
                    ) == true

            object : XC_MethodReplacement() {
                override fun replaceHookedMethod(param: MethodHookParam): Any {
                    val workspaceItemInfo =
                        callMethod(param.thisObject, "getItemInfo")
                    val componentName = callMethod(
                        workspaceItemInfo,
                        "getTargetComponent"
                    ) as ComponentName
                    val pkg = componentName.packageName

                    log("pkg=$pkg")

                    val recentsView =
                        callMethod(param.thisObject, "getRecentsView")
                    callMethod(recentsView, "finishRecentsAnimation", false, Runnable {
                        AndroidAppHelper.currentApplication().sendBroadcast(
                            Intent("com.chenyue404.motohook.freeform")
                                .putExtra("package", pkg)
                                .putExtra("bubble", showAsBubble)
                        )
                    })

//                    Handler(Looper.getMainLooper()).postDelayed({
//                        AndroidAppHelper.currentApplication().sendBroadcast(
//                            Intent("com.chenyue404.motohook.freeform")
//                                .putExtra("package", pkg)
//                        )
//                    }, 300)

                    return true
                }
            }.let {
                if (PluginEntry.isAndroid13) {
                    findAndHookMethod(
                        "com.android.quickstep.views.TaskView",
                        classLoader,
                        "lambda\$setIcon\$8\$com-android-quickstep-views-TaskView",
                        findClass("com.android.quickstep.views.IconView", classLoader),
                        View::class.java,
                        it
                    )
                } else {
                    findAndHookMethod(
                        "com.android.quickstep.views.TaskView", classLoader,
                        "lambda\$setIcon\$6\$TaskView",
                        View::class.java,
                        it
                    )
                }
            }
        }

        if (PluginEntry.isAndroid13
            && PluginEntry.pref?.getBoolean("key_Launcher_replace_split_to_share", false) == true
        ) {
            findAndHookMethod(
                "com.android.quickstep.views.OverviewActionsView",
                classLoader,
                "setSplitButtonVisible",
                Boolean::class.java,
                object : XC_MethodHook() {
                    override fun beforeHookedMethod(param: MethodHookParam) {
                        param.args[0] = true
                        (getObjectField(param.thisObject, "mSplitButton") as Button)
                            .setText("分享截图")
                    }
                }
            )
            findAndHookMethod(
                "com.android.quickstep.TaskOverlayFactory.TaskOverlay",
                classLoader,
                "enterSplitSelect",
                object : XC_MethodReplacement() {
                    override fun replaceHookedMethod(param: MethodHookParam): Any? {
                        log("拆分->分享截图")
                        val mImageApi = getObjectField(param.thisObject, "mImageApi")
                        callMethod(
                            mImageApi,
                            "startShareActivity",
                            arrayOf(Rect::class.java),
                            null
                        )
                        return null
                    }
                }
            )
        }
    }

    private fun log(str: String) {
        XposedBridge.log("$TAG$str")
    }
}
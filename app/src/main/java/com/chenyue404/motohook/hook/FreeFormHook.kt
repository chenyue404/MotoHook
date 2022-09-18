package com.chenyue404.motohook.hook

import android.view.View
import android.widget.ImageView
import androidx.core.view.isVisible
import de.robv.android.xposed.*
import de.robv.android.xposed.XposedHelpers.findAndHookMethod
import de.robv.android.xposed.callbacks.XC_LoadPackage

/**
 * Created by chenyue on 2022/9/18 0018.
 */
class FreeFormHook : IXposedHookLoadPackage {

    private val PACKAGE_NAME = "com.android.systemui"
    private val TAG = "Moto-systemui-hook-"

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {

        val packageName = lpparam.packageName
        val classLoader = lpparam.classLoader

        if (packageName != PACKAGE_NAME) {
            return
        }

        log("")

        //隐藏电池图标
        findAndHookMethod(
            "com.android.systemui.BatteryMeterView", classLoader,
            "scaleBatteryMeterViews",
            object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam) {
                    log("com.android.systemui.BatteryMeterView#scaleBatteryMeterViews")
                    val mBatteryIconView =
                        XposedHelpers.findField(param.thisObject.javaClass, "mBatteryIconView")
                            .get(param.thisObject) as ImageView
                    log("获取mBatteryIconView")
                    mBatteryIconView.isVisible = false
                }
            }
        )

        //长按最近任务按钮打开菜单
        findAndHookMethod("com.android.systemui.navigationbar.NavigationBar",
            classLoader,
            "updateScreenPinningGestures",
            object : XC_MethodReplacement() {
                override fun replaceHookedMethod(param: MethodHookParam) {
                    log("com.android.systemui.navigationbar.NavigationBar#updateScreenPinningGestures")
                    val mNavigationBarView =
                        XposedHelpers.findField(param.thisObject.javaClass, "mNavigationBarView")
                            .get(param.thisObject)
                    log("获取mNavigationBarView")
                    val recentButton =
                        XposedHelpers.callMethod(mNavigationBarView, "getRecentsButton")
                    log("获取recentButton")
                    XposedHelpers.callMethod(
                        recentButton,
                        "setOnLongClickListener",
                        object : View.OnLongClickListener {
                            override fun onLongClick(v: View): Boolean {
                                log("recentButton长按")
                                Runtime.getRuntime().exec("input keyevent 82")
                                return true
                            }
                        })
                }
            })
    }

    private fun log(str: String) {
        XposedBridge.log("$TAG$str")
    }
}
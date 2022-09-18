package com.chenyue404.motohook.hook

import android.widget.ImageView
import androidx.core.view.isVisible
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.XposedHelpers.findAndHookMethod
import de.robv.android.xposed.callbacks.XC_LoadPackage

/**
 * Created by chenyue on 2022/9/18 0018.
 */
class BatteryHook : IXposedHookLoadPackage {

    private val PACKAGE_NAME = "com.android.systemui"
    private val TAG = "Moto-systemui-hook-"

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {

        val packageName = lpparam.packageName
        val classLoader = lpparam.classLoader

        if (packageName != PACKAGE_NAME) {
            return
        }

        log(TAG)

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
    }

    private fun log(str: String) {
        XposedBridge.log(str)
    }
}
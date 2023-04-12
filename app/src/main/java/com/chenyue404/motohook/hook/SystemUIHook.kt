package com.chenyue404.motohook.hook

import android.app.AndroidAppHelper
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.view.View
import android.widget.ImageView
import androidx.core.view.isVisible
import com.chenyue404.motohook.PluginEntry
import de.robv.android.xposed.*
import de.robv.android.xposed.XposedHelpers.findAndHookMethod
import de.robv.android.xposed.callbacks.XC_LoadPackage

/**
 * Created by chenyue on 2022/9/18 0018.
 */
class SystemUIHook : IXposedHookLoadPackage {

    private val PACKAGE_NAME = "com.android.systemui"
    private val TAG = "Moto-hook-systemui-"

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {

        val packageName = lpparam.packageName
        val classLoader = lpparam.classLoader

        if (packageName != PACKAGE_NAME) {
            return
        }

        log("")

        val isAndroid13 = Build.VERSION.SDK_INT > Build.VERSION_CODES.S_V2

        if (PluginEntry.pref?.getBoolean(
                "key_SystemUi_hide_battery_icon_when_not_charging", false
            ) == true
        ) {
            //隐藏电池图标，充电时显示
            object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam) {
                    log("com.android.systemui.BatteryMeterView#onBatteryLevelChanged")
                    val args = param.args
                    val mBatteryIconView =
                        XposedHelpers.findField(param.thisObject.javaClass, "mBatteryIconView")
                            .get(param.thisObject) as ImageView
                    mBatteryIconView.isVisible = args[1] as Boolean
                }
            }.let {
                if (isAndroid13) {
                    findAndHookMethod(
                        "com.android.systemui.battery.BatteryMeterView", classLoader,
                        "onBatteryLevelChanged",
                        Int::class.java,
                        Boolean::class.java,
                        it
                    )
                } else {
                    findAndHookMethod(
                        "com.android.systemui.BatteryMeterView", classLoader,
                        "onBatteryLevelChanged",
                        Int::class.java,
                        Boolean::class.java,
                        Boolean::class.java,
                        it
                    )
                }
            }
        }

        //长按最近任务按钮打开菜单
        if (PluginEntry.pref?.getBoolean(
                "key_SystemUi_long_press_recent_is_menu", false
            ) == true
        ) {
            findAndHookMethod("com.android.systemui.navigationbar.NavigationBar",
                classLoader,
                "updateScreenPinningGestures",
                object : XC_MethodReplacement() {
                    override fun replaceHookedMethod(param: MethodHookParam) {
                        log("com.android.systemui.navigationbar.NavigationBar#updateScreenPinningGestures")
                        val mNavigationBarView =
                            XposedHelpers.findField(
                                param.thisObject.javaClass,
                                if (isAndroid13) "mView" else "mNavigationBarView"
                            ).get(param.thisObject)
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

        //自定义广播接收器打开单手模式
        if (PluginEntry.pref?.getBoolean(
                "key_SystemUi_receive_broadcast_open_one_hand_mode", false
            ) == true
        ) {
            findAndHookMethod("com.android.wm.shell.onehanded.OneHandedController", classLoader,
                "registerSettingObservers",
                Int::class.java,
                object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam) {
                        log("com.android.wm.shell.onehanded.OneHandedController")
                        AndroidAppHelper.currentApplication()
                            .registerReceiver(object : BroadcastReceiver() {
                                override fun onReceive(context: Context?, intent: Intent?) {
                                    log("收到单手广播")
                                    XposedHelpers.callMethod(param.thisObject, "startOneHanded")
                                }
                            }, IntentFilter("com.chenyue404.motohook.onehanded"))
                    }
                })
        }
    }

    private fun log(str: String) {
        XposedBridge.log("$TAG$str")
    }
}
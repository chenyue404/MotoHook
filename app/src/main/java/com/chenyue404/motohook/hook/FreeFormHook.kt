package com.chenyue404.motohook.hook

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.chenyue404.motohook.PluginEntry
import de.robv.android.xposed.*
import de.robv.android.xposed.XposedHelpers.findAndHookMethod
import de.robv.android.xposed.callbacks.XC_LoadPackage

/**
 * Created by chenyue on 2022/9/18 0018.
 */
class FreeFormHook : IXposedHookLoadPackage {

    private val PACKAGE_NAME = "com.motorola.freeform"
    private val TAG = "Moto-hook-freeform-"

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {

        val packageName = lpparam.packageName
        val classLoader = lpparam.classLoader

        if (packageName != PACKAGE_NAME) {
            return
        }

        log("")


        if (false) {
            findAndHookMethod(
                "b.c.a.a.a", classLoader,
                "s",
                Int::class.java,
                object : XC_MethodReplacement() {
                    override fun replaceHookedMethod(p0: MethodHookParam?): Any {
                        return true
                    }
                }
            )
        }

        var receiver: BroadcastReceiver? = null

        if (PluginEntry.pref?.getBoolean("key_freeform_receive_broadcast_open", false) == true) {
            findAndHookMethod(
                Application::class.java,
                "onCreate",
                object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam) {
                        log("Application onCreate after")

                        if (receiver == null) {
                            receiver = object : BroadcastReceiver() {
                                override fun onReceive(context: Context, intent: Intent) {
                                    val packageName = intent.extras?.getString("package")
                                    log(packageName ?: return)

                                    val clazz_cb =
                                        XposedHelpers.findClassIfExists("b.d.a.j.c.b", classLoader)
                                    if (clazz_cb == null) {
                                        log("clazz_b is null")
                                        return
                                    }

                                    val class_ca = XposedHelpers.getStaticObjectField(clazz_cb, "a")
                                    if (class_ca == null) {
                                        log("class_a is null")
                                        return
                                    }
                                    XposedHelpers.callMethod(
                                        class_ca,
                                        "a",
                                        packageName
                                    )

                                    val clazz_bc =
                                        XposedHelpers.findClassIfExists("b.d.a.j.b.c", classLoader)
                                    if (clazz_bc == null) {
                                        log("clazz_b is null")
                                        return
                                    }

                                    val class_ba = XposedHelpers.getStaticObjectField(clazz_bc, "a")
                                    if (class_ba == null) {
                                        log("class_a is null")
                                        return
                                    }

//                                    Handler(Looper.getMainLooper()).postDelayed({
                                        XposedHelpers.callMethod(
                                            class_ba,
                                            "c",
                                            packageName
                                        )
//                                    }, 300)
                                }
                            }
                        }

                        (param.thisObject as Application).applicationContext
                            .registerReceiver(
                                receiver,
                                IntentFilter("com.chenyue404.motohook.freeform")
                            )
                    }
                }
            )
        }
    }

    private fun log(str: String) {
        XposedBridge.log("$TAG$str")
    }
}
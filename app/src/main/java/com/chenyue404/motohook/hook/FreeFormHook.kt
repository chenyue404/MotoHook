package com.chenyue404.motohook.hook

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
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

        var receiver: BroadcastReceiver? = null

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

//                                val clazz_b =
//                                    XposedHelpers.findClassIfExists("b.d.a.j.c.b", classLoader)
//                                if (clazz_b == null) {
//                                    log("clazz_b is null")
//                                    return
//                                }
//
//                                val class_a = XposedHelpers.getStaticObjectField(clazz_b, "a")
//                                if (class_a == null) {
//                                    log("class_a is null")
//                                    return
//                                }
//
//                                XposedHelpers.callMethod(
//                                    class_a,
//                                    "a",
//                                    packageName
//                                )

                                val clazz_c =
                                    XposedHelpers.findClassIfExists("b.d.a.j.b.c", classLoader)
                                if (clazz_c == null) {
                                    log("clazz_b is null")
                                    return
                                }

                                val class_a = XposedHelpers.getStaticObjectField(clazz_c, "a")
                                if (class_a == null) {
                                    log("class_a is null")
                                    return
                                }

                                XposedHelpers.callMethod(
                                    class_a,
                                    "c",
                                    packageName
                                )
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

    private fun log(str: String) {
        XposedBridge.log("$TAG$str")
    }
}
package com.chenyue404.motohook.hook

import android.os.Bundle
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.XposedHelpers.findAndHookMethod
import de.robv.android.xposed.callbacks.XC_LoadPackage

/**
 * Created by chenyue on 2022/9/18 0018.
 */
class AssistantHook : IXposedHookLoadPackage {

    private val PACKAGE_NAME = "com.lenovo.menu_assistant"
    private val TAG = "Moto-hook-assistant-"

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {

        val packageName = lpparam.packageName
        val classLoader = lpparam.classLoader

        if (packageName != PACKAGE_NAME) {
            return
        }

        log("")

//        findAndHookMethod(
//            "is", classLoader,
//            "n2",
//            String::class.java,
//            object : XC_MethodHook() {
//                override fun beforeHookedMethod(param: MethodHookParam) {
//                    log("defpackage.is#n2")
//                    val str = param.args.first().toString()
//                    log(str)
//                }
//            }
//        )

//        findAndHookMethod("is", classLoader,
//            "M1",
//            Bundle::class.java,
//            object : XC_MethodHook() {
//                override fun afterHookedMethod(param: MethodHookParam) {
//                    log("defpackage.is#M1")
//                    val bundle = param.args.first() as Bundle
//                    log(bundle.toString())
//                    log(param.result as String)
//                }
//            })

        findAndHookMethod("is", classLoader,
            "onResults",
            Bundle::class.java,
            object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam) {
                    log("defpackage.is#onResults")
                    val bundle = param.args.first() as Bundle
                    log(bundle.toString())

                    val getStr = XposedHelpers.callMethod(param.thisObject, "M1", bundle) as String?
                    log(getStr ?: "未解析到")
                    if (getStr == "这是一个测试") {
                        try {
                            Runtime.getRuntime().exec("input keyevent 4")
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        param.result = null
                    }
                }
            })
    }

    private fun log(str: String) {
        XposedBridge.log("$TAG$str")
    }
}
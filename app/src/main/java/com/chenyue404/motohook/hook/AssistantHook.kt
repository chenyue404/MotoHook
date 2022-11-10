package com.chenyue404.motohook.hook

import android.app.AndroidAppHelper
import android.content.Intent
import android.os.Bundle
import com.chenyue404.motohook.BuildConfig
import com.chenyue404.motohook.PluginEntry
import com.chenyue404.motohook.ui.CustomVoiceCommandActivity
import de.robv.android.xposed.*
import de.robv.android.xposed.XposedHelpers.findAndHookMethod
import de.robv.android.xposed.callbacks.XC_LoadPackage
import org.json.JSONArray
import org.json.JSONException
import java.util.regex.Pattern

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

        if (PluginEntry.pref?.getBoolean("key_assistant_use_custom_directive", false) == true) {
            findAndHookMethod(
                "a50", classLoader,
                "onResults",
                Bundle::class.java,
                object : XC_MethodHook() {
                    override fun beforeHookedMethod(param: MethodHookParam) {
//                        log("defpackage.is#onResults")
                        val bundle = param.args.first() as Bundle
//                    log(bundle.toString())

                        val actionStr =
                            XposedHelpers.callMethod(param.thisObject, "N1", bundle) as String?

                        val matchCustomCommand =
                            actionStr?.let {
                                getCustomCommands().indexOfFirst {
                                    Pattern.compile(it).matcher(actionStr).find()
                                } != -1
                            } ?: false

                        if (matchCustomCommand) {
                            log("匹配自定义指令$actionStr")
                            try {
                                AndroidAppHelper.currentApplication()
                                    .sendBroadcast(
                                        Intent("com.chenyue404.motohook.assistant.custom.command")
                                            .putExtra("text", actionStr)
                                    )
                                Runtime.getRuntime().exec("input keyevent 4")
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                            param.result = null
                        }
                    }
                })
        }
        if (BuildConfig.DEBUG) {
            findAndHookMethod(
                "com.lenovo.lasf.util.Log", classLoader,
                "isLoggable",
                String::class.java,
                Int::class.java,
                object : XC_MethodReplacement() {
                    override fun replaceHookedMethod(param: MethodHookParam?): Boolean {
                        return true
                    }
                }
            )
        }

//        var textReceiver: BroadcastReceiver? = null
//        val textReceiverAction = "com.chenyue404.motohook.assistant.text"
//        findAndHookMethod(
//            "is#", classLoader,
//            "onAttachedToWindow",
//            object : XC_MethodHook() {
//                override fun afterHookedMethod(param: MethodHookParam) {
//                    textReceiver = object : BroadcastReceiver() {
//                        override fun onReceive(context: Context, intent: Intent) {
//                            val str = intent.extras?.getString("text")
//                            if (str.isNullOrEmpty()) {
//                                log("空Str")
//                                return
//                            }
//                            XposedHelpers.callMethod(param.thisObject, "n2", str)
//                        }
//                    }
//                    AndroidAppHelper.currentApplication()
//                        .registerReceiver(textReceiver, IntentFilter(textReceiverAction))
//                }
//            }
//        )
//        findAndHookMethod(
//            "is#", classLoader,
//            "onDetachedFromWindow",
//            object : XC_MethodHook() {
//                override fun afterHookedMethod(param: MethodHookParam) {
//                    AndroidAppHelper.currentApplication().unregisterReceiver(textReceiver)
//                }
//            }
//        )
    }

    private fun log(str: String) {
        XposedBridge.log("$TAG$str")
    }

    private fun getCustomCommands(): List<String> {
        return PluginEntry.pref?.getString(CustomVoiceCommandActivity.KEY_VOICE_COMMANDS, null)
            ?.let {
                try {
                    JSONArray(it)
                } catch (e: JSONException) {
                    null
                }
            }?.let {
                val list = mutableListOf<String>()
                for (i in 0 until it.length()) {
                    val rule = it.getString(i)
                    list.add(rule)
                }
                list.toList()
            } ?: listOf()
    }
}
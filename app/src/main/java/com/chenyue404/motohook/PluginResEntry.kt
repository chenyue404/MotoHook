package com.chenyue404.motohook

import com.chenyue404.motohook.hook.SettingsResHook
import de.robv.android.xposed.IXposedHookInitPackageResources
import de.robv.android.xposed.callbacks.XC_InitPackageResources

/**
 * Created by chenyue on 2022/9/18 0018.
 */
class PluginResEntry : IXposedHookInitPackageResources {
    override fun handleInitPackageResources(resparam: XC_InitPackageResources.InitPackageResourcesParam) {
        listOf(
            SettingsResHook()
        ).forEach {
            it.handleInitPackageResources(resparam)
        }
    }
}
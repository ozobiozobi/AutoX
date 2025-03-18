package com.aiselp.autox.api

import android.content.Context
import com.caoccao.javet.annotations.V8Function
import com.caoccao.javet.interop.V8Runtime
import com.caoccao.javet.values.reference.V8ValueObject
import com.stardust.autojs.AutoJs
import com.stardust.autojs.runtime.api.AppUtils

class JsApp(context: Context) : NativeApi {
    override val moduleId: String = "app"

    @get:V8Function
    val appUtils = try {
        AutoJs.instance.appUtils
    } catch (e: Exception) {
        AppUtils(context)
    }

    override fun install(v8Runtime: V8Runtime, global: V8ValueObject): NativeApi.BindingMode {

        return NativeApi.BindingMode.ObjectBind
    }

    override fun recycle(v8Runtime: V8Runtime, global: V8ValueObject) {}

    @V8Function
    fun viewFile(path: String) {
        appUtils.viewFile(path)
    }

    @V8Function
    fun editFile(path: String) {
        appUtils.editFile(path)
    }


}
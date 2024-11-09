package com.aiselp.autox.api

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.caoccao.javet.annotations.V8Function
import com.caoccao.javet.interop.V8Runtime
import com.caoccao.javet.values.reference.V8ValueObject

class JsFloaty : NativeApi {
    override val moduleId: String = ID

    override fun install(v8Runtime: V8Runtime, global: V8ValueObject): NativeApi.BindingMode {
        return NativeApi.BindingMode.PROXY
    }

    override fun recycle(v8Runtime: V8Runtime, global: V8ValueObject) {
    }

    @V8Function
    fun createFloaty(context: Context, view: View, params: ViewGroup.LayoutParams) {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.addView(view, params)
    }

    @V8Function
    fun createLayoutParams(): WindowManager.LayoutParams {
        val layoutParams = WindowManager.LayoutParams()

        return layoutParams
    }

    companion object {
        const val ID = "floaty"
    }
}
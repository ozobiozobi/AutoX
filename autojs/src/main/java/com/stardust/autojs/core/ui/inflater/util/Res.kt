package com.stardust.autojs.core.ui.inflater.util

import android.content.Context
import android.view.View
import com.stardust.autojs.R

/**
 * Created by Stardust on 2017/11/5.
 */
object Res {
    private val style by lazy {
        R.style::class.java
    }

    @JvmStatic
    fun parseStyle(view: View, value: String): Int {
        return parseStyle(view.context, value)
    }

    @JvmStatic
    fun parseStyle(context: Context?, value: String): Int {
        // FIXME: 2017/11/5 Can or should it retrieve android.R.style or styleable?
        var s = value
        if (value.startsWith("@style/")) {
            s = value.substring(7)
        }
        return style.getField(s.replace(".", "_")).getInt(null)
    }
}

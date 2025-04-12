package com.stardust.autojs.core.ui.inflater.util

import android.content.Context
import android.view.View
import com.stardust.autojs.R

/**
 * Created by Stardust on 2017/11/4.
 */
object Strings {
    private val string by lazy {
        R.string::class.java
    }

    fun parse(context: Context, str: String): String {
        if (str.startsWith("@string/")) {
            val name = str.substring(8).replace("", "_")
            val id = string.getField(name).getInt(null)
            return context.getString(id)
        }
        return str
    }

    @JvmStatic
    fun parse(view: View, str: String): String {
        return parse(view.context, str)
    }
}

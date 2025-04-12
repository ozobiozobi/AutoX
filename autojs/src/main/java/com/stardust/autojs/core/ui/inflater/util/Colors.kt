package com.stardust.autojs.core.ui.inflater.util

import android.content.Context
import android.view.View
import androidx.core.graphics.toColorInt
import com.stardust.autojs.R

/**
 * Created by Stardust on 2017/11/3.
 */
object Colors {
    private val colorClass by lazy {
        R.color::class.java
    }

    @JvmStatic
    fun parse(context: Context, color: String): Int {
        if (color.startsWith("@color/")) {
            val name = color.substring(7).replace(".", "_")
            return context.getColor(colorClass.getField(name).getInt(null))
        }
        if (color.startsWith("@android:color/")) {
            return color.substring(15).toColorInt()
        }
        return color.toColorInt()
    }

    @JvmStatic
    fun parse(view: View, color: String): Int {
        return parse(view.context, color)
    }
}

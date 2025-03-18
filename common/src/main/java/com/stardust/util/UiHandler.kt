package com.stardust.util

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast

/**
 * Created by Stardust on 2017/5/2.
 */
class UiHandler(val context: Context) : Handler(Looper.getMainLooper()) {
    fun toast(message: String?) {
        post {
            Toast.makeText(
                context,
                message.toString(),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun toast(resId: Int) {
        post { Toast.makeText(context, resId, Toast.LENGTH_SHORT).show() }
    }
}

package com.stardust.autojs.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContract
import com.stardust.autojs.R
import com.stardust.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout

/**
 * Created by Stardust on 2018/1/30.
 */
object FloatingPermission {
    private const val OP_SYSTEM_ALERT_WINDOW = 24

    fun ensurePermissionGranted(context: Context): Boolean {
        if (!canDrawOverlays(context)) {
            toast(context, R.string.text_no_floating_window_permission)
            manageDrawOverlays(context)
            return false
        }
        return true
    }

    fun waitForPermissionGranted(context: Context) {
        if (canDrawOverlays(context)) {
            return
        }
        runBlocking {
            withContext(Dispatchers.Main) {
                manageDrawOverlays(context)
                toast(context, R.string.text_no_floating_window_permission)
            }
            withTimeout(20 * 1000) {
                while (true) {
                    if (canDrawOverlays(context)) break
                    delay(200)
                }
            }
        }
    }


    @JvmStatic
    fun manageDrawOverlays(context: Context) {
        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
        intent.setData(Uri.parse("package:" + context.packageName))
        context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    @JvmStatic
    fun canDrawOverlays(context: Context): Boolean {
        return Settings.canDrawOverlays(context);
    }

    class FloatingPermissionActivityResultContract(val context: Context) :
        ActivityResultContract<Unit, Boolean>() {
        override fun createIntent(context: Context, input: Unit): Intent {
            return Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:${context.packageName}")
            )

        }

        override fun parseResult(resultCode: Int, intent: Intent?): Boolean {
            return canDrawOverlays(context)
        }

    }

}

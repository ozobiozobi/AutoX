package com.stardust.autojs.runtime.api

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.stardust.autojs.annotation.ScriptInterface
import com.stardust.util.IntentUtil
import java.lang.ref.WeakReference
import kotlin.concurrent.Volatile

/**
 * Created by Stardust on 2017/4/2.
 */
class AppUtils {
    private val mContext: Context

    @Volatile
    private var mCurrentActivity = WeakReference<Activity?>(null)

    @get:ScriptInterface
    val fileProviderAuthority: String?
    var currentActivity: Activity?
        get() {
            Log.d("App", "getCurrentActivity: " + mCurrentActivity.get())
            return mCurrentActivity.get()
        }
        set(currentActivity) {
            mCurrentActivity = WeakReference(currentActivity)
            Log.d("App", "setCurrentActivity: $currentActivity")
        }

    init {

    }

    constructor(context: Context) : this(context, null)

    constructor(context: Context, fileProviderAuthority: String?) {
        mContext = context
        this.fileProviderAuthority = fileProviderAuthority
    }

    @ScriptInterface
    fun launchPackage(packageName: String): Boolean {
        try {
            val packageManager = mContext.packageManager
            mContext.startActivity(
                packageManager.getLaunchIntentForPackage(packageName)!!
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
            return true
        } catch (e: Exception) {
            return false
        }
    }

    @ScriptInterface
    fun sendLocalBroadcastSync(intent: Intent) {
        LocalBroadcastManager.getInstance(mContext).sendBroadcastSync(intent)
    }

    @ScriptInterface
    fun launchApp(appName: String): Boolean {
        val pkg = getPackageName(appName) ?: return false
        return launchPackage(pkg)
    }
    @ScriptInterface
    fun getInstalledPackages(flags: Int): List<PackageInfo> {
        return mContext.packageManager.getInstalledPackages(flags)
    }

    @ScriptInterface
    fun getPackageName(appName: String): String? {
        val packageManager = mContext.packageManager
        val installedApplications =
            packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        for (applicationInfo in installedApplications) {
            if (packageManager.getApplicationLabel(applicationInfo).toString() == appName) {
                return applicationInfo.packageName
            }
        }
        return null
    }

    @ScriptInterface
    fun getAppName(packageName: String): String? {
        val packageManager = mContext.packageManager
        try {
            val applicationInfo = packageManager.getApplicationInfo(packageName, 0)
            val appName = packageManager.getApplicationLabel(applicationInfo)
            return appName.toString()
        } catch (e: PackageManager.NameNotFoundException) {
            return null
        }
    }

    @ScriptInterface
    fun openAppSetting(packageName: String?): Boolean {
        return IntentUtil.goToAppDetailSettings(mContext, packageName)
    }


    @ScriptInterface
    fun uninstall(packageName: String) {
        mContext.startActivity(
            Intent(
                Intent.ACTION_DELETE, "package:$packageName".toUri()
            ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }

    @ScriptInterface
    fun viewFile(path: String?) {
        if (path == null) throw NullPointerException("path == null")
        IntentUtil.viewFile(mContext, path, fileProviderAuthority)
    }

    @ScriptInterface
    fun editFile(path: String?) {
        if (path == null) throw NullPointerException("path == null")
        IntentUtil.editFile(mContext, path, fileProviderAuthority)
    }

    @ScriptInterface
    fun getUriForFile(path: String): Uri {
        var uri = path.toUri()
        if (uri.scheme != "file") {
            uri = "file://${uri.path}".toUri()
        }
        if (fileProviderAuthority == null) {
            return uri
        }
        return FileProvider.getUriForFile(mContext, fileProviderAuthority!!, uri.toFile())
    }

    @ScriptInterface
    fun openUrl(url: String) {
        val uri = if (!url.startsWith("http://") && !url.startsWith("https://")) {
            "http://$url".toUri()
        } else url.toUri()
        mContext.startActivity(
            Intent(Intent.ACTION_VIEW)
                .setData(uri)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }
}

package com.stardust.autojs.core.shizuku

import android.content.ComponentName
import android.content.pm.PackageManager
import android.os.RemoteException
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.stardust.autojs.BuildConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import rikka.shizuku.Shizuku
import rikka.shizuku.Shizuku.UserServiceArgs


class ShizukuClient private constructor() : Shizuku.OnRequestPermissionResultListener,
    Shizuku.OnBinderDeadListener, Shizuku.OnBinderReceivedListener {

    private val scope = CoroutineScope(Dispatchers.Main)
    var shizukuConnection: ShizukuConnection? = null
    private var packageName: String? = null

    var available: Boolean by mutableStateOf(false)
    var userPermission: Boolean by mutableStateOf(false)

    init {
        scope.launch {
            available = Shizuku.pingBinder()
            userPermission = checkPermission()
        }
    }

    fun checkPermission(): Boolean {
        if (Shizuku.isPreV11()) {
            return false
        }
        if (!Shizuku.pingBinder()) return false

        return if (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) {
            // Granted
            true
        } else if (Shizuku.shouldShowRequestPermissionRationale()) {
            // Users choose "Deny and don't ask again"
            false
        } else {
            false
        }
    }

    fun setupService(packageName: String) = scope.launch {
        if (available && shizukuConnection == null) {
            bindService(packageName)
        }
        this@ShizukuClient.packageName = packageName
    }

    fun checkConnection() {
        checkNotNull(shizukuConnection) { RemoteException("Shizuku未连接") }
    }

    suspend fun ensureShizukuService(): IShizukuUserService {
        checkConnection()
        return withTimeout(1000) {
            shizukuConnection!!.binder.join()
            shizukuConnection!!.service!!
        }
    }


    fun bindService(packageName: String) {
        Log.d(TAG, "bindService")
        val userServiceArgs = UserServiceArgs(
            ComponentName(packageName, ShizukuUserService::class.java.getName())
        )
            .daemon(false)
            .processNameSuffix("service")
            .debuggable(BuildConfig.DEBUG)
            .version(BuildConfig.VERSION_CODE)
        shizukuConnection = ShizukuConnection()
        Shizuku.bindUserService(userServiceArgs, shizukuConnection!!)
    }

    override fun onRequestPermissionResult(requestCode: Int, grantResult: Int) {
        if (requestCode == REQUEST_CODE) {
            scope.launch { userPermission = grantResult == PackageManager.PERMISSION_GRANTED }
        }
    }

    override fun onBinderDead() {
        Log.d(TAG, "onBinderDead")
        scope.launch {
            available = true
            if (shizukuConnection == null && packageName != null) {
                bindService(packageName!!)
            }
        }
    }

    override fun onBinderReceived() {
        Log.d(TAG, "onBinderReceived")
        scope.launch {
            available = false
            shizukuConnection = null
        }
    }

    companion object {
        private const val TAG = "ShizukuClient"
        private const val REQUEST_CODE = 1564
        const val SHIZUKU_PACKAGE_NAME = "moe.shizuku.privileged.api"

        fun requestPermission() {
            Shizuku.requestPermission(REQUEST_CODE)
        }

        val instance by lazy {
            val client = ShizukuClient()
            Shizuku.addRequestPermissionResultListener(client)
            Shizuku.addBinderDeadListener(client)
            Shizuku.addBinderReceivedListener(client)
            client
        }
    }


}
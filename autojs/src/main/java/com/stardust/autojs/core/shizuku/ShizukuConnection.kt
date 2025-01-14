package com.stardust.autojs.core.shizuku

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import android.os.RemoteException
import android.util.Log
import kotlinx.coroutines.Job

open class ShizukuConnection : ServiceConnection {
    var service: IShizukuUserService? = null
    var binder = Job().apply { completeExceptionally(RemoteException("Shizuku service unavailable")) }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        Log.d(TAG, "onServiceConnected service: $service")
        this.service = IShizukuUserService.Stub.asInterface(service)
        binder.complete()
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        service = null
        binder = Job().apply {
            completeExceptionally(RemoteException("Shizuku service disconnected"))
        }
    }


    companion object {
        private const val TAG = "ShizukuConnection"
    }
}
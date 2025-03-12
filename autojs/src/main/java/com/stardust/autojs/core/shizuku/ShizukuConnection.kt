package com.stardust.autojs.core.shizuku

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import android.os.RemoteException
import android.util.Log
import com.stardust.autojs.core.console.LogEntry
import com.stardust.autojs.servicecomponents.BinderConsoleListener
import kotlinx.coroutines.Job

open class ShizukuConnection : ServiceConnection {
    val binderConsoleListener = BinderConsoleListener.ClientInterface()
    var service: IShizukuUserService? = null
    var binder =
        Job().apply { completeExceptionally(RemoteException("Shizuku service unavailable")) }


    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        Log.d(TAG, "onServiceConnected service: $service")
        this.service = IShizukuUserService.Stub.asInterface(service)
        this.service!!.setConsole(binderConsoleListener)
        binder.complete()
        binderConsoleListener.logPublish.onNext(
            LogEntry(level = Log.INFO, content = "Shizuku service connected")
        )
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        service = null
        binder = Job().apply {
            completeExceptionally(RemoteException("Shizuku service disconnected"))
        }
        binderConsoleListener.logPublish.onNext(
            LogEntry(level = Log.ERROR, content = "Shizuku service disconnected")
        )
    }


    companion object {
        private const val TAG = "ShizukuConnection"
    }
}
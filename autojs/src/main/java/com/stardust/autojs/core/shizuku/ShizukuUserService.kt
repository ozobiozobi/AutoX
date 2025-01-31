package com.stardust.autojs.core.shizuku

import android.content.Context
import android.util.Log
import com.stardust.autojs.core.util.Shell2
import com.stardust.autojs.core.util.toJson
import kotlin.system.exitProcess


class ShizukuUserService : IShizukuUserService.Stub {
    private lateinit var context: Context

    private val shells = mutableMapOf<Int, Shell2>()

    init {

    }

    constructor() : super()
    constructor(context: Context) : this() {
        this.context = context
    }


    override fun destroy() {
        Log.i(TAG, "destroy")
        shells.forEach { (_, shell) ->
            shell.exit()
        }
        shells.clear()
        exitProcess(0)
    }

    override fun exit() = destroy()
    override fun runShellCommand(id: Int, command: String): String {
        val shell = synchronized(shells) {
            val shell = shells[id] ?: Shell2("sh")
            shells[id] = shell
            shell
        }
        return shell.execAndWaitFor(command).toJson()
    }

    override fun recycleShell(id: Int) {
        synchronized(shells) {
            shells.remove(id)?.exit()
        }
    }

    override fun getPackageName(): String {
        return context.packageName
    }

    companion object {
        private const val TAG = "ShizukuUserService"
    }
}
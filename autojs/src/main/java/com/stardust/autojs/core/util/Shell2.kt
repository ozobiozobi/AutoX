package com.stardust.autojs.core.util

import android.os.Process
import android.util.Log
import com.google.gson.Gson
import com.stardust.autojs.annotation.ScriptInterface
import com.stardust.autojs.runtime.api.AbstractShell
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import java.io.Closeable
import java.util.concurrent.Executors
import com.jaredrummler.ktsh.Shell as Ktsh

class Shell2(initCommand: String) : Closeable {
    private val sh = Ktsh(initCommand)
    private var callback: Shell.Callback? = null
    private var pid: Int? = null
    private val executor = Executors.newSingleThreadExecutor()
    private val scope = CoroutineScope(executor.asCoroutineDispatcher())
    private val l = object : Ktsh.OnLineListener {
        override fun onLine(line: String) {
            if (pid == null && line.endsWith(PidSuffix)) {
                pid = line.replace(PidSuffix, "").toInt()
                Log.d(TAG, "create shell pid: $pid")
                initStatus.complete()
                return
            }
            callback?.onOutput(line)
            callback?.onNewLine(line)
        }
    }
    private val initStatus = Job()

    init {
        sh.addOnStderrLineListener(l)
        sh.addOnStdoutLineListener(l)
        scope.launch {
            sh.run("echo $$${PidSuffix}")
        }
    }

    @ScriptInterface
    fun exec(cmd: String) {
        scope.launch { sh.run(cmd) }
    }

    @ScriptInterface
    fun execAndWaitFor(cmd: String): AbstractShell.Result = runBlocking {
        val r = scope.async { sh.run(cmd) }.await()
        AbstractShell.Result().apply {
            code = r.exitCode
            error = r.stderr()
            result = r.stdout()
        }
    }

    fun isAlive(): Boolean = sh.isAlive()

    @ScriptInterface
    fun exit() = runBlocking {
        withTimeout(2000) {
            initStatus.join()
            exitSubprocess(pid!!)
            Process.killProcess(pid!!)
        }
        close()
    }

    private fun exitSubprocess(pid: Int) {
        val result = Ktsh.SH.run("pgrep -P $pid")
        result.stdout().split("\n").forEach { p ->
            try {
                val sPid = p.toInt()
                exitSubprocess(sPid)
                Process.killProcess(sPid)
            } catch (e: Exception) {
                Log.w(TAG, e)
            }
        }
    }

    @ScriptInterface
    fun setCallback(callback: Shell.Callback) {
        this.callback = callback
    }

    @ScriptInterface
    fun exitAndWaitFor() {
        sh.shutdown()
        close()
    }

    override fun close() {
        if (!executor.isShutdown) {
            executor.shutdownNow()
        }
    }

    companion object {
        private const val PidSuffix = "-pid------00eaweesd"
        private const val TAG = "Shell2"
        fun fromResultJson(json: String): AbstractShell.Result = Gson().fromJson(json, AbstractShell.Result::class.java)
    }
}

fun AbstractShell.Result.toJson(): String = Gson().toJson(this)

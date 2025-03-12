package com.stardust.autojs.runtime.api

import com.stardust.autojs.annotation.ScriptInterface
import com.stardust.autojs.core.shizuku.ShizukuClient
import kotlinx.coroutines.runBlocking

class Shizuku {

    @ScriptInterface
    fun runRhinoScriptFile(path: String) = runBlocking {
        val shizukuService = ShizukuClient.instance.ensureShizukuService()
        shizukuService.runRhinoScriptFile(path)
    }

    @ScriptInterface
    fun runRhinoScript(script: String) = runBlocking {
        val shizukuService = ShizukuClient.instance.ensureShizukuService()
        shizukuService.runRhinoScript(script)
    }

    @ScriptInterface
    fun isShizukuAlive(): Boolean = ShizukuClient.instance.shizukuConnection.service != null

    companion object {

    }
}
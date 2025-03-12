package com.stardust.autojs.core.shizuku

import com.stardust.autojs.core.util.Shell2
import com.stardust.autojs.rhino.AndroidContextFactory
import org.mozilla.javascript.Context
import org.mozilla.javascript.ContextFactory
import org.mozilla.javascript.ImporterTopLevel
import org.mozilla.javascript.Script
import org.mozilla.javascript.Scriptable
import org.mozilla.javascript.ScriptableObject
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean

class RhinoEngine(val context: android.content.Context) {
    val globalScope = ImporterTopLevel()
    private val initStatus = AtomicBoolean(false)

    init {
        if (!ContextFactory.hasExplicitGlobal()) {
            val shell2 = Shell2("sh")
            val result = shell2.execAndWaitFor("mktemp -d")
            shell2.exit()
            ContextFactory.initGlobal(
                AndroidContextFactory(
                    File(result.result.trim())
                )
            )
        }
    }

    fun initGlobalScope(globalScope: ImporterTopLevel) {
        setGlobalProperty("context", context)
        setGlobalProperty("global", globalScope)
    }

    fun setGlobalProperty(key: String, value: Any?) {
        ScriptableObject.putProperty(globalScope, key, value)
    }

    fun createExecutionScope(context: Context): Scriptable {
        val scriptable = context.newObject(globalScope)
        scriptable.parentScope = globalScope
        return scriptable
    }

    private fun enterContext(): Context {
        return Context.enter()
    }

    fun execScript(script: Script): Any? {
        val context = enterContext()
        if (!initStatus.getAndSet(true)) {
            globalScope.initStandardObjects(context, false)
            initGlobalScope(globalScope)
        }
        try {
            return script.exec(context, createExecutionScope(context))
        } finally {
            Context.exit()
        }
    }

    fun runScriptString(script: String, name: String = "anonymous"): Any? {
        val compileScript = enterContext().compileString(script, name, 1, null)
        return execScript(compileScript)
    }

    fun runScriptFile(path: File, name: String = path.path): Any? {
        val compileScript = enterContext().compileReader(path.reader(), name, 1, null)
        return execScript(compileScript)
    }

    class ResultReceiver {
        var data: String? = null
        fun setResult(result: String?) {
            data = result
        }
    }
}
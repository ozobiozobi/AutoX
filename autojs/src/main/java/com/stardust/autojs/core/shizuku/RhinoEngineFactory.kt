package com.stardust.autojs.core.shizuku

import android.content.Context
import android.util.Log
import com.stardust.autojs.core.console.LogEntry
import com.stardust.autojs.runtime.ScriptRuntimeV2
import com.stardust.autojs.servicecomponents.BinderConsoleListener

class RhinoEngineFactory(val context: Context) {
    val console = Console()

    fun setAutojsConsole(console: BinderConsoleListener) {
        this.console.console = console
    }

    fun createRhinoEngine(): RhinoEngine {
        return RhinoEngine(context).apply {
            setGlobalProperty("console", console)
        }
    }

    inline fun catchErrorAndPrint(action: () -> Unit) {
        try {
            action.invoke()
        } catch (e: Exception) {
            console.error(ScriptRuntimeV2.getStackTrace(e, false))
        }
    }

    fun setResultReceiver(engine: RhinoEngine, resultReceiver: RhinoEngine.ResultReceiver) {
        engine.setGlobalProperty("resultReceiver", resultReceiver)
        engine.runScriptString(
            """
            (()=>{
                var r = global.resultReceiver
                global.resultReceiver = {
                    setData(data){
                        data = JSON.stringify(data);
                        r.setResult(data)
                    }
                }
            })()
        """.trimIndent()
        )
    }

    class Console {
        var console: BinderConsoleListener? = null
        fun log(vararg data: Any?) {
            console?.onPrintln(LogEntry(-1, Log.DEBUG, data.joinToString(" "), true))
        }

        fun error(vararg data: Any?) {
            console?.onPrintln(LogEntry(-1, Log.ERROR, data.joinToString(" "), true))
        }

        fun warn(vararg data: Any?) {
            console?.onPrintln(LogEntry(-1, Log.WARN, data.joinToString(" "), true))
        }

        fun info(vararg data: Any?) {
            console?.onPrintln(LogEntry(-1, Log.INFO, data.joinToString(" "), true))
        }

        fun debug(vararg data: Any?) {
            console?.onPrintln(LogEntry(-1, Log.DEBUG, data.joinToString(" "), true))
        }

        fun verbose(vararg data: Any?) {
            console?.onPrintln(LogEntry(-1, Log.VERBOSE, data.joinToString(" "), true))
        }
    }
}
package org.autojs.autojs.ui.log

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.aiselp.autox.engine.NodeScriptSource
import com.aiselp.autox.utils.loadScriptTask
import com.stardust.autojs.script.JavaScriptFileSource
import com.stardust.autojs.servicecomponents.EngineController

class LogActivityKt : LogActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val task = loadScriptTask(intent, savedInstanceState)
        if (task != null) {
            val source = task.source
            val file = when (source) {
                is JavaScriptFileSource -> source.file
                is NodeScriptSource -> source.file
                else -> null
            }
            if (file != null) {
                EngineController.runScript(file, null, task.config)
            }
        }
    }

    companion object {
        @JvmStatic
        fun start(context: Context) {
            context.startActivity(Intent(context, LogActivityKt::class.java))
        }
    }
}
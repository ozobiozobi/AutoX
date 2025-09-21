package com.aiselp.autox.utils

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import com.aiselp.autox.engine.NodeScriptSource
import com.stardust.autojs.ScriptEngineService
import com.stardust.autojs.execution.ExecutionConfig
import com.stardust.autojs.execution.ScriptExecution
import com.stardust.autojs.execution.ScriptExecutionTask
import com.stardust.autojs.script.JavaScriptFileSource
import com.stardust.autojs.script.ScriptFile
import com.stardust.autojs.script.ScriptSource

private const val fileKey = "source-file"
private const val configKey = "config"
private const val intentKey = "script-extra"

fun Bundle.saveScriptExecute(source: ScriptSource, config: ExecutionConfig?) {
    if (source is JavaScriptFileSource) {
        putString(fileKey, source.file.path)
    }else if (source is NodeScriptSource){
        putString(fileKey, source.file.path)
    }
    putParcelable(configKey, config)
}

fun Bundle.restoreScriptTask(): ScriptExecutionTask? {
    val file = getString(fileKey) ?: return null
    val config = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getParcelable(configKey, ExecutionConfig::class.java)
    } else {
        getParcelable(configKey)
    } ?: ExecutionConfig(workingDirectory = ScriptFile(file).parent ?: "/")
    val task = ScriptExecutionTask(ScriptFile(file).toSource(), null, config)
    ScriptEngineService.instance?.setupExecutionTaskListener(task)
    return task
}

fun loadScriptTask(intent: Intent? = null, bundle: Bundle?): ScriptExecutionTask? {
    return intent?.let {
        val bundle = it.extras?.get(intentKey)
        when (bundle) {
            is Bundle -> bundle
            is PersistableBundle -> Bundle(bundle)
            else -> null
        }?.restoreScriptTask()
    } ?: bundle?.restoreScriptTask()
}

fun loadScriptExecute(intent: Intent? = null, bundle: Bundle?): ScriptExecution? {
    val task = loadScriptTask(intent, bundle) ?: return null
    val execution = ScriptEngineService.instance?.createScriptExecution(task)
    return execution
}

fun setupStartIntent(intent: Intent, source: ScriptSource, config: ExecutionConfig?) {
    intent.putExtra(intentKey, Bundle().apply {
        saveScriptExecute(source, config)
    })
}
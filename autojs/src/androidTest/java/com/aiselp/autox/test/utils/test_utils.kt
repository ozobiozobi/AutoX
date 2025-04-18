package com.aiselp.autox.test.utils

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.aiselp.autox.engine.NodeScriptEngine
import com.stardust.autojs.engine.RhinoJavaScriptEngine
import com.stardust.autojs.engine.ScriptEngine
import org.mozilla.javascript.ScriptableObject
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.writeText


fun ScriptEngine<*>.getGlobalProperty(key: String): Any? {
    if (this is RhinoJavaScriptEngine) {
        return ScriptableObject.getProperty(this.scriptable, key)
    }
    if (this is NodeScriptEngine) {
        return runtime.globalObject.getPropertyObject(key)
    }

    throw NotImplementedError()
}

fun Any?.toDouble(): Double? {
    when (this) {
        is Double -> return this
        is Number -> return this.toDouble()
        else -> return null
    }
}

fun createTestFile(name: String, content: String): Path {
    val application: Application = ApplicationProvider.getApplicationContext()
    val tempDirectory =
        Files.createDirectories(
            application.cacheDir.toPath().resolve("test_script"),
        )
    val file = tempDirectory.resolve(name)
    file.writeText(content)
    return file
}

fun openScriptAsset(context: android.content.Context, assetName: String): Path {
    val tempDirectory =
        Files.createTempDirectory(context.cacheDir.toPath(), "test_script")
    val file = tempDirectory.resolve(assetName)
    Files.createDirectories(file.parent)
    context.assets.open(assetName).use {
        Files.write(file, it.readBytes())
        return file
    }
}
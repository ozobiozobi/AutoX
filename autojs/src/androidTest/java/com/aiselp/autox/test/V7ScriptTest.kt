package com.aiselp.autox.test

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.aiselp.autox.test.utils.createTestFile
import com.stardust.autojs.AutoJs
import com.stardust.autojs.ScriptResultViewer
import com.stardust.autojs.TestAutojs
import com.stardust.autojs.execution.ScriptExecution
import com.stardust.autojs.execution.ScriptExecutionListener
import com.stardust.autojs.script.ScriptFile
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith
import java.nio.file.Path

@RunWith(AndroidJUnit4::class)
@MediumTest
class V7ScriptTest {
    val application: Application = ApplicationProvider.getApplicationContext()

    private fun runScript(file: Path, l: ScriptExecutionListener): ScriptExecution {
        return AutoJs.instance.scriptEngineService.execute(
            ScriptFile(file.toString()).toSource(), l
        )
    }

    @Test
    fun bast_test(): Unit = runBlocking {
        val file = createTestFile(
            "bast.mjs", """
            import assert from "assert"
            globalThis.a = 1;
            assert(a === 1)
            """
        )
        val resultViewer = ScriptResultViewer()
        val execution = runScript(file, resultViewer)
        resultViewer.waitForFinish(1000) { execution.engine.forceStop() }

        val resultViewer2 = ScriptResultViewer()
        val file2 = createTestFile("bast.mjs", "throw new Error('123')")
        val execution2 = runScript(file2, resultViewer2)
        resultViewer2.waitForFinish(1000) { execution2.engine.forceStop() }
        assert(resultViewer2.result.isCancelled)
    }

    companion object {
        val v7AccessDir = "v7_test_script"

        init {
            val application: Application = ApplicationProvider.getApplicationContext()
            TestAutojs.init(application)
        }
    }
}
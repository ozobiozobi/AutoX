package com.stardust.autojs

import android.app.Application
import com.stardust.app.GlobalAppContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.util.concurrent.atomic.AtomicBoolean

class TestAutojs(application: Application) : AutoJs(application) {

    override fun ensureAccessibilityServiceEnabled() {
        TODO("Not yet implemented")
    }

    override fun waitForAccessibilityServiceEnabled() {
        TODO("Not yet implemented")
    }

    companion object {
        private val init = AtomicBoolean(false)
        fun init(application: Application) {
            if (init.getAndSet(true)) return
            GlobalAppContext.set(
                application,
                com.stardust.app.BuildConfig(
                    true, "org.autojs.autoxjs", VERSION_CODE = BuildConfig.VERSION_CODE.toLong(),
                )
            )
            runBlocking {
                withContext(Dispatchers.Main) {
                    instance = TestAutojs(application)
                }
            }
        }
    }
}
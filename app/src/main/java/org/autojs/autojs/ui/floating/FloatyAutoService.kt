package org.autojs.autojs.ui.floating

import android.content.Intent
import android.os.IBinder
import com.stardust.app.service.AbstractAutoService
import com.stardust.enhancedfloaty.FloatyService

class FloatyAutoService : AbstractAutoService() {
    private var circularMenu: CircularMenu? = null
    override fun onCreate() {
        super.onCreate()
        startService(Intent(this, FloatyService::class.java))
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        if (action == SHOW_CIRCULAR_MENU) {
            if (circularMenu == null) {
                circularMenu = CircularMenu(this)
            }
        } else if (action == HIED_CIRCULAR_MENU) {
            circularMenu?.close()
            circularMenu = null
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        circularMenu?.close()
        circularMenu = null
    }

    companion object {
        const val SHOW_CIRCULAR_MENU = "org.autojs.autojs.ui.floating.CircularMenu.show"
        const val HIED_CIRCULAR_MENU = "org.autojs.autojs.ui.floating.CircularMenu.hide"
    }
}
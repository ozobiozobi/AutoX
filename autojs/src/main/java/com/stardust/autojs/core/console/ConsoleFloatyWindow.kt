package com.stardust.autojs.core.console

import android.view.WindowManager
import com.stardust.autojs.servicecomponents.EngineController
import com.stardust.enhancedfloaty.FloatyService
import com.stardust.enhancedfloaty.ResizableExpandableFloaty
import com.stardust.enhancedfloaty.ResizableExpandableFloatyWindow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ConsoleFloatyWindow(floaty: ResizableExpandableFloaty) :
    ResizableExpandableFloatyWindow(floaty) {
    val scope = EngineController.scope

    @Volatile
    var isShown = false
    override fun onCreate(service: FloatyService, manager: WindowManager) {
        super.onCreate(service, manager)
        expand()
        windowBridge.updatePosition(0, 0)
    }

    fun show() {
        if (isShown) return
        val window = this
        scope.launch(Dispatchers.Main) {
            FloatyService.addWindow(window)
            isShown = true
        }
    }

    fun hide() {
        if (!isShown) return
        scope.launch(Dispatchers.Main) {
            close()
            isShown = false
        }
    }

    fun setTouchable(touchable: Boolean) {
        val windowLayoutParams: WindowManager.LayoutParams = windowLayoutParams
        if (touchable) {
            windowLayoutParams.flags =
                windowLayoutParams.flags and WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE.inv()
        } else {
            windowLayoutParams.flags =
                windowLayoutParams.flags or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        }
        scope.launch(Dispatchers.Main) {
            updateWindowLayoutParams(windowLayoutParams)
        }
    }
}
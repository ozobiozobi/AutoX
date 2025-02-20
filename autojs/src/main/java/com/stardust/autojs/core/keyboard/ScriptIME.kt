package com.stardust.autojs.core.keyboard

import android.inputmethodservice.InputMethodService
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import com.stardust.autojs.R
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.atomic.AtomicBoolean

class ScriptIME : InputMethodService() {

    override fun onCreateInputView(): View? {
        return layoutInflater.inflate(R.layout.keyboard_view, null)
    }

    override fun onStartInput(attribute: EditorInfo?, restarting: Boolean) {
        super.onStartInput(attribute, restarting)
        Log.i(TAG, "onStartInput")
        keyboardOperation = KeyboardOperation(this.currentInputConnection)
    }

    override fun onFinishInput() {
        super.onFinishInput()
        Log.i(TAG, "onFinishInput")
        keyboardOperation = null
    }

    override fun onStartInputView(editorInfo: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(editorInfo, restarting)
        startView.set(true)
        do {
            val text = textInputQueue.poll()
            if (text != null) {
                keyboardOperation?.inputText(text)
            }
        } while (text != null)
    }

    override fun onFinishInputView(finishingInput: Boolean) {
        super.onFinishInputView(finishingInput)
        startView.set(false)
    }

    companion object {
        @Volatile
        var keyboardOperation: KeyboardOperation? = null
            private set
        private const val TAG = "ScriptIME"
        val startView = AtomicBoolean(false)
        val textInputQueue = ArrayBlockingQueue<String>(10000)
    }
}
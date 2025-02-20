package com.stardust.autojs.runtime.api

import com.stardust.autojs.annotation.ScriptInterface
import com.stardust.autojs.core.keyboard.KeyboardOperation
import com.stardust.autojs.core.keyboard.ScriptIME

class Keyboard {

    val open: Boolean
        get() = ScriptIME.startView.get()
    val available: Boolean
        get() = ScriptIME.keyboardOperation != null

    private fun openKeyboard(operate: (KeyboardOperation) -> Boolean): Boolean {
        val k = ScriptIME.keyboardOperation ?: return false
        return operate(k)
    }

    @ScriptInterface
    fun addInputQueue(text: String) {
        val k = ScriptIME.keyboardOperation
        if (k != null) {
            k.inputText(text)
        } else {
            ScriptIME.textInputQueue.add(text)
        }
    }

    @ScriptInterface
    fun clearInputQueue() {
        ScriptIME.textInputQueue.clear()
    }

    @ScriptInterface
    fun inputText(text: String): Boolean = openKeyboard {
        it.inputText(text)
    }

    @ScriptInterface
    fun downKeyCode(code: Int): Boolean = openKeyboard {
        it.downKeyCode(code)
    }

    @ScriptInterface
    fun upKeyCode(code: Int): Boolean = openKeyboard {
        it.upKeyCode(code)
    }

    @ScriptInterface
    fun getInputText(): String {
        val k = ScriptIME.keyboardOperation ?: throw RuntimeException("ime not open")
        return k.getInputText()
    }

    @JvmOverloads
    @ScriptInterface
    fun setSelection(start: Int, end: Int = start) = openKeyboard {
        it.inputConnection.setSelection(start, end)
    }

    @ScriptInterface
    fun clearInput(): Boolean = openKeyboard {
        it.clearInput()
    }

    @JvmOverloads
    @ScriptInterface
    fun deleteSurroundingText(beforeLength: Int, afterLength: Int = 0) = openKeyboard {
        it.inputConnection.deleteSurroundingText(beforeLength, afterLength)
    }

    @ScriptInterface
    fun getSelectedText(): String? {
        return ScriptIME.keyboardOperation?.getSelectedText()?.toString()
    }

}
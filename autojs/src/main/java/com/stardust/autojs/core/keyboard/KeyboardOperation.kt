package com.stardust.autojs.core.keyboard

import android.view.KeyEvent
import android.view.inputmethod.ExtractedTextRequest
import android.view.inputmethod.InputConnection


class KeyboardOperation(val inputConnection: InputConnection) {


    init {
        inputConnection
    }

    fun inputText(text: String): Boolean {
        return inputConnection.commitText(text, 1)
    }

    fun downKeyCode(code: Int): Boolean {
        return inputConnection.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, code))
    }

    fun upKeyCode(code: Int): Boolean {
        return inputConnection.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, code))
    }

    fun getInputText(): String {
        val curPos: CharSequence = inputConnection.getExtractedText(ExtractedTextRequest(), 0).text
        val beforePos = inputConnection.getTextBeforeCursor(curPos.length, 0)
        val afterPos = inputConnection.getTextAfterCursor(curPos.length, 0)
        return if (beforePos !== null && afterPos !== null) {
            afterPos.toString() + beforePos
        } else ""
    }

    fun clearInput(): Boolean {
        val curPos: CharSequence = inputConnection.getExtractedText(ExtractedTextRequest(), 0).text
        val beforePos = inputConnection.getTextBeforeCursor(curPos.length, 0)
        val afterPos = inputConnection.getTextAfterCursor(curPos.length, 0)
        return if (beforePos !== null && afterPos !== null) {
            inputConnection.deleteSurroundingText(beforePos.length, afterPos.length)
        } else false
    }

    fun getSelectedText(): CharSequence? {
        return inputConnection.getSelectedText(0)
    }
}
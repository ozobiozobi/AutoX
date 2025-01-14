package com.stardust.autojs.runtime.exception

class UIBlockingException(message: String = "Cannot run blocking operations on the UI thread") :
    ScriptException(message) {
}
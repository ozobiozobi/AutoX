package com.stardust.autojs.core.pref

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.stardust.app.GlobalAppContext.get
import com.stardust.autojs.core.pref.PrefKey.KEY_FOREGROUND_SERVICE

object Pref {
    private var inr: SharedPreferences? = null
    private val preferences: SharedPreferences
        get() = inr ?: PreferenceManager.getDefaultSharedPreferences(get())


    val isStableModeEnabled: Boolean
        get() {
            return preferences.getBoolean("key_stable_mode", false)
        }

    val isGestureObservingEnabled: Boolean
        get() {
            return preferences.getBoolean("key_gesture_observing", false)
        }

    val isForegroundServiceEnabled: Boolean
        get() {
            return preferences.getBoolean(KEY_FOREGROUND_SERVICE, false)
        }

    fun getDefault(context: Context): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }

    fun init(context: Context) {
        inr = getDefault(context)
    }
}

object PrefKey {
    const val KEY_STABLE_MODE = "key_stable_mode"
    const val KEY_GESTURE_OBSERVING = "key_gesture_observing"
    const val KEY_AUTO_BACKUP = "key_auto_backup"
    const val KEY_FOREGROUND_SERVICE = "key_foreground_service"
    const val KEY_USE_VOLUME_CONTROL_RECORD = "key_use_volume_control_record"
    const val KEY_LANGUAGE = "key_language"
}
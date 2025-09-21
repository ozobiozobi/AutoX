package com.stardust.view.accessibility

import android.util.Log
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.stardust.event.EventDispatcher
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.coroutines.Job
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import java.util.TreeMap
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Created by Stardust on 2017/5/2.
 */


open class AccessibilityService : android.accessibilityservice.AccessibilityService() {

    interface GestureListener {
        fun onGesture(gestureId: Int)
    }

    private val keyObserver = PublishSubject.create<KeyEvent>()
    val onKeyObserver = OnKeyListener.Observer()
    val keyInterrupterObserver = KeyInterceptor.Observer()
    val gestureEventDispatcher = EventDispatcher<GestureListener>()

    private var mFastRootInActiveWindow: AccessibilityNodeInfo? = null

    //事件执行线程池，异步执行要注意事件对象可能会被回收或修改
    private val eventExecutor: ExecutorService = Executors.newSingleThreadExecutor()

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        instance = this
        // Log.v(TAG, "onAccessibilityEvent: $event");
        if (filterEventTypes?.contains(event.eventType) == false)
            return
        val type = event.eventType
        if (type == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED || type == AccessibilityEvent.TYPE_VIEW_FOCUSED) {
            val root = rootInActiveWindow
            if (root != null) {
                mFastRootInActiveWindow = root
            }
        }

        for ((_, delegate) in mDelegates) {
            if (delegate.eventTypes?.contains(event.eventType) == false)
                continue
            if (delegate.onAccessibilityEvent(this@AccessibilityService, event))
                break
        }
    }


    override fun onInterrupt() {

    }

    override fun onKeyEvent(event: KeyEvent): Boolean {
        val eventClone = KeyEvent(event)
        Log.v(TAG, "onKeyEvent: $eventClone")
        eventExecutor.execute {
            stickOnKeyObserver.onKeyEvent(eventClone.keyCode, eventClone)
            onKeyObserver.onKeyEvent(eventClone.keyCode, eventClone)
            keyObserver.onNext(eventClone)
        }
        return keyInterrupterObserver.onInterceptKeyEvent(event)
    }

    @Deprecated("Deprecated in Java")
    override fun onGesture(gestureId: Int): Boolean {
        eventExecutor.execute {
            gestureEventDispatcher.dispatchEvent {
                it.onGesture(gestureId)
            }
        }
        return false
    }

    override fun getRootInActiveWindow(): AccessibilityNodeInfo? {
        return try {
            super.getRootInActiveWindow()
        } catch (e: Exception) {
            null
        }

    }

    override fun onDestroy() {
        Log.v(TAG, "onDestroy: $instance")
        ENABLED = Job()
        instance = null
        eventExecutor.shutdownNow()
        super.onDestroy()
    }


    override fun onServiceConnected() {
        Log.v(TAG, "onServiceConnected: $serviceInfo")
        instance = this
        super.onServiceConnected()
        ENABLED.complete()
        // FIXME: 2017/2/12 有时在无障碍中开启服务后这里不会调用服务也不会运行，安卓的BUG???
    }


    fun fastRootInActiveWindow(): AccessibilityNodeInfo? {
        return mFastRootInActiveWindow
    }

    companion object {

        private const val TAG = "AccessibilityService"

        private val mDelegates = TreeMap<Int, AccessibilityDelegate>()

        @Volatile
        private var ENABLED = Job()
        var instance: AccessibilityService? = null
            private set
        val stickOnKeyObserver = OnKeyListener.Observer()
        private var filterEventTypes: HashSet<Int>? = HashSet()

        fun addDelegate(uniquePriority: Int, delegate: AccessibilityDelegate) {
            mDelegates[uniquePriority] = delegate
            val set = delegate.eventTypes
            if (set == null) filterEventTypes = null
            else filterEventTypes?.addAll(set)
        }

        fun disable(): Boolean {
            instance?.disableSelf()
            return true
        }

        fun waitForEnabled(timeOut: Long): Boolean = runBlocking {
            if (instance != null) return@runBlocking true
            if (timeOut == -1L) {
                ENABLED.join();true
            } else withTimeoutOrNull(timeOut) {
                ENABLED.join();true
            } != null
        }
    }


}

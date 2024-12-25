package com.stardust.notification

import android.service.notification.StatusBarNotification
import com.stardust.view.accessibility.NotificationListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Created by Stardust on 2017/10/30.
 */

class NotificationListenerService : android.service.notification.NotificationListenerService() {

    private val mNotificationListeners = CopyOnWriteArrayList<NotificationListener>()
    private val scope = CoroutineScope(Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    override fun onNotificationPosted(sbn: StatusBarNotification, rankingMap: RankingMap) {
        if (mNotificationListeners.isEmpty()) return
        scope.launch {
            for (listener in mNotificationListeners) {
                listener.onNotification(
                    Notification.create(
                        sbn.notification, sbn.packageName))
            }
        }
    }


    override fun onNotificationRemoved(sbn: StatusBarNotification, rankingMap: RankingMap) {}

    fun addListener(listener: NotificationListener) {
        mNotificationListeners.add(listener)
    }

    fun removeListener(listener: NotificationListener): Boolean {
        return mNotificationListeners.remove(listener)
    }


    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
        instance = null
    }

    companion object {
        var instance: NotificationListenerService? = null
            private set
    }
}

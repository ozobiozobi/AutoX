package com.stardust.autojs.core.image.capture

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.IBinder
import com.stardust.app.OnActivityResultDelegate
import kotlinx.coroutines.CompletableDeferred
import java.util.concurrent.CancellationException

class ScreenCaptureManager : ScreenCaptureRequester {
    @Volatile
    override var screenCapture: ScreenCapturer? = null
    private var mediaProjection: MediaProjection? = null

    override suspend fun requestScreenCapture(context: Context, orientation: Int) {
        if (screenCapture?.available == true) {
            screenCapture?.setOrientation(orientation, context)
            return
        }

        val result = if (context is OnActivityResultDelegate.DelegateHost && context is Activity) {
            ScreenCaptureRequester.ActivityScreenCaptureRequester(
                context.onActivityResultDelegateMediator, context
            ).request()
        } else {
            val result = CompletableDeferred<Intent>()
            ScreenCaptureRequestActivity.request(context) { data ->
                if (data != null) {
                    result.complete(data)
                } else result.cancel(CancellationException("data is null"))
            }
            result.await()
        }

        // 使用服务绑定确保服务就绪
        val serviceConnected = CompletableDeferred<Unit>()
        val connection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                try {
                    // 服务已连接，安全获取mediaProjection
                    mediaProjection =
                        (context.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager)
                            .getMediaProjection(Activity.RESULT_OK, result)
                    CaptureForegroundService.setMediaProjection(context, mediaProjection!!)
                    screenCapture = ScreenCapturer(mediaProjection!!, orientation)
                } finally {
                    serviceConnected.complete(Unit)
                    context.unbindService(this)
                }
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                // 服务意外断开时处理
                serviceConnected.completeExceptionally(IllegalStateException("Service disconnected unexpectedly"))
            }
        }

        // 绑定服务并等待连接
        context.startService(Intent(context, CaptureForegroundService::class.java))
        context.bindService(
            Intent(context, CaptureForegroundService::class.java),
            connection,
            Context.BIND_AUTO_CREATE
        )
        serviceConnected.await()
    }

    override fun recycle() {
        screenCapture?.release()
        screenCapture = null
        mediaProjection?.stop()
        mediaProjection = null
    }
}
package com.stardust.autojs.core.opencv

import android.content.Context
import kotlinx.coroutines.Job
import org.opencv.android.OpenCVLoader

/**
 * Created by Stardust on 2018/4/2.
 */
object OpenCVHelper {
    private const val LOG_TAG = "OpenCVHelper"

    val isInitialized = Job()

    @JvmStatic
    fun newMatOfPoint(mat: Mat?): MatOfPoint {
        return MatOfPoint(mat)
    }

    @JvmStatic
    fun release(mat: MatOfPoint?) {
        if (mat == null) return
        mat.release()
    }

    @JvmStatic
    fun release(mat: Mat?) {
        if (mat == null) return
        mat.release()
    }

    //初始化OpenCV耗时很小，可以忽略不做额外处理
    @Synchronized
    fun initIfNeeded(context: Context?) {
        if (isInitialized.isCompleted) {
            return
        }
        OpenCVLoader.initLocal()
        isInitialized.complete()
    }
}

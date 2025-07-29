package org.autojs.autojs.build

import android.content.Context
import java.io.File
import java.io.IOException
import java.io.InputStream

/**
 * Created by Stardust on 2017/11/29.
 * Modified by wilinz on 2022/5/23
 */
object ApkBuilderPluginHelper {
    private const val TEMPLATE_APK_PATH = "template.apk"
    fun openTemplateApk(context: Context): InputStream? {
        if (apkFile(context).isFile) {
            try {
                return apkFile(context).inputStream()
            } catch (_: IOException) {
            }
        }
        try {
            return context.assets.open(TEMPLATE_APK_PATH)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    fun checkTemplateApkAsset(context: Context): Boolean {
        return try {
            if (apkFile(context).isFile)
                return true
            context.assets.open(TEMPLATE_APK_PATH).use { it.read() }
            true
        } catch (e: IOException) {
            false
        }
    }

    fun setTemplateApkAsset(context: Context, inputStream: InputStream) {
        inputStream.use { inp ->
            apkFile(context).outputStream().use { out ->
                inp.copyTo(out)
            }
        }
    }

    private fun apkFile(context: Context): File {
        return File(context.filesDir, "template.apk")
    }
}
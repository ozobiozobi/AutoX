package com.stardust.auojs.inrt

import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.edit
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import com.aiselp.autox.engine.NodeScriptEngine
import com.aiselp.autox.ui.material3.theme.AppTheme
import com.google.gson.Gson
import com.stardust.auojs.inrt.autojs.AutoJs
import com.stardust.auojs.inrt.launch.GlobalProjectLauncher
import com.stardust.autojs.project.ProjectConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.autojs.autoxjs.inrt.R

/**
 * Created by Stardust on 2018/2/2.
 * Modified by wilinz on 2022/5/23
 */

class SplashActivity : AppCompatActivity() {
    val permissionCheck = PermissionCheck()

    companion object {
        const val TAG = "SplashActivity"
    }

    private val appVersionChange by lazy {
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        val appVersion = packageManager.getPackageInfo(packageName, 0).versionCode
        val l = pref.getLong(Pref.KEY_APP_VERSION, -1)
        pref.edit { putLong(Pref.KEY_APP_VERSION, appVersion.toLong()) }
        appVersion.toLong() != l
    }

    private lateinit var projectConfig: ProjectConfig


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes = window.attributes.apply {
                layoutInDisplayCutoutMode =
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            }
        }
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowCompat.getInsetsController(window, window.decorView)
        controller.hide(WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        var slug by mutableStateOf(getString(R.string.powered_by_autojs))
        setContent {
            AppTheme(dynamicColor = true) {
                permissionCheck.Dialog()
                Column(
                    Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surface),
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            modifier = Modifier.size(120.dp),
                            painter = painterResource(R.drawable.autojs_logo),
                            contentDescription = null
                        )
                    }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                        Text(
                            slug,
                            fontSize = 14.sp,
                            fontFamily = FontFamily(
                                Typeface.createFromAsset(assets, "roboto_medium.ttf")
                            ),
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                    }
                }
            }
        }
        lifecycleScope.launch {
            projectConfig = withContext(Dispatchers.IO) {
                ProjectConfig.fromAssets(
                    this@SplashActivity,
                    ProjectConfig.configFileOfDir("project")
                )!!
            }
            if (projectConfig.launchConfig.displaySplash) {
//                val frame = findViewById<FrameLayout>(R.id.frame)
//                frame.visibility = View.VISIBLE
            }
            Log.d(TAG, "onCreate: ${Gson().toJson(projectConfig)}")
            slug = projectConfig.launchConfig.splashText
            if (appVersionChange) { //非第一次运行
                projectConfig.launchConfig.let {
                    Pref.setHideLogs(it.isHideLogs)
                    Pref.setStableMode(it.isStableMode)
                    Pref.setStopAllScriptsWhenVolumeUp(it.isVolumeUpControl)
                    Pref.setDisplaySplash(it.displaySplash)
                }

            }
            val initModuleResource = launch(Dispatchers.IO) {
                NodeScriptEngine.initModuleResource(this@SplashActivity, appVersionChange)
            }
            if (projectConfig.launchConfig.displaySplash) {
                delay(1000)
            }
            initModuleResource.join()
            if (permissionCheck.checkPermission(
                    this@SplashActivity, projectConfig.launchConfig.permissions
                )
            ) {
                runScript()
            } else {
                permissionCheck.requestPermission(
                    this@SplashActivity, projectConfig.launchConfig.permissions
                ) { runScript() }
            }
        }
    }

    private fun runScript() {
        Thread {
            try {
                GlobalProjectLauncher.launch(this)
                this.finish()
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this@SplashActivity, e.message, Toast.LENGTH_LONG).show()
                    startActivity(Intent(this@SplashActivity, LogActivity::class.java))
                    AutoJs.instance.globalConsole.printAllStackTrace(e)
                }
            }
        }.start()
    }

}


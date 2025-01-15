package com.stardust.auojs.inrt

import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import com.aiselp.autox.engine.NodeScriptEngine
import com.aiselp.autox.ui.material3.components.AlertDialog
import com.aiselp.autox.ui.material3.components.DialogController
import com.aiselp.autox.ui.material3.theme.AppTheme
import com.google.gson.Gson
import com.stardust.app.GlobalAppContext
import com.stardust.app.permission.BackgroundStartPermission
import com.stardust.app.permission.DrawOverlaysPermission
import com.stardust.app.permission.DrawOverlaysPermission.launchCanDrawOverlaysSettings
import com.stardust.app.permission.Permissions
import com.stardust.app.permission.PermissionsSettingsUtil.launchAppPermissionsSettings
import com.stardust.auojs.inrt.autojs.AccessibilityServiceTool
import com.stardust.auojs.inrt.autojs.AccessibilityServiceTool1
import com.stardust.auojs.inrt.autojs.AutoJs
import com.stardust.auojs.inrt.launch.GlobalProjectLauncher
import com.stardust.autojs.project.ProjectConfig
import com.stardust.autojs.util.PermissionUtil
import com.stardust.autojs.util.StoragePermissionResultContract
import com.stardust.toast
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

    companion object {
        const val TAG = "SplashActivity"
    }

    private val appVersionChange by lazy {
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        val appVersion = packageManager.getPackageInfo(packageName, 0).versionCode
        val l = pref.getLong(Pref.KEY_APP_VERSION, -1)
        pref.edit().putLong(Pref.KEY_APP_VERSION, appVersion.toLong()).apply()
        appVersion.toLong() != l
    }
    private val accessibilitySettingsLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            checkAccessibilityServices()
            checkSpecialPermissions()
        }

    private fun checkAccessibilityServices() {
        if (AccessibilityServiceTool.isAccessibilityServiceEnabled(this)) {
            permissionsResult[Permissions.ACCESSIBILITY_SERVICES] = true
            toast(this, getString(R.string.text_accessibility_service_turned_on))
        } else {
            toast(this, getString(R.string.text_accessibility_service_is_not_turned_on))
        }
    }

    private val backgroundStartSettingsLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (BackgroundStartPermission.isBackgroundStartAllowed(this)) {
                permissionsResult[Permissions.BACKGROUND_START] = true
            }
            checkSpecialPermissions()
        }

    private val drawOverlaysSettingsLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (DrawOverlaysPermission.isCanDrawOverlays(this)) {
                permissionsResult[Permissions.DRAW_OVERLAY] = true
            }
            checkSpecialPermissions()
        }

    @RequiresApi(Build.VERSION_CODES.R)
    private val storagePermissionLauncher =
        registerForActivityResult(StoragePermissionResultContract()) {
            checkSpecialPermissions()
        }

    private lateinit var projectConfig: ProjectConfig

    private val permissionsResult = mutableMapOf<String, Boolean>()

    private fun checkSpecialPermissions() {
        if (permissionsResult.all { it.value }) {
            runScript()
        } else {
            for ((key, value) in permissionsResult) {
                if (!value) {
                    when (key) {
                        Permissions.ACCESSIBILITY_SERVICES -> {
                            lifecycleScope.launch { requestAccessibilityServiceDialog.show() }
                        }

                        Permissions.BACKGROUND_START -> {
                            lifecycleScope.launch { requestBackgroundStartDialog.show() }
                        }

                        Permissions.DRAW_OVERLAY -> {
                            lifecycleScope.launch { requestDrawOverlaysDialog.show() }
                        }
                    }
                    break
                }
            }
        }
    }


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
                requestDrawOverlaysDialog.Dialog()
                requestAccessibilityServiceDialog.Dialog()
                requestBackgroundStartDialog.Dialog()
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
            readSpecialPermissionConfiguration()
            requestExternalStoragePermission()
        }
    }

    private fun readSpecialPermissionConfiguration() {
        projectConfig.launchConfig.permissions.forEach { permission ->
            when (permission) {
                Permissions.ACCESSIBILITY_SERVICES -> {
                    permissionsResult[permission] =
                        AccessibilityServiceTool.isAccessibilityServiceEnabled(this)
                }

                Permissions.BACKGROUND_START -> {
                    permissionsResult[permission] =
                        BackgroundStartPermission.isBackgroundStartAllowed(this)
                }

                Permissions.DRAW_OVERLAY -> {
                    permissionsResult[permission] = DrawOverlaysPermission.isCanDrawOverlays(this)
                }
            }
        }
    }

    private fun requestExternalStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !PermissionUtil.checkStoragePermission()) {
            PermissionUtil.showPermissionDialog(this) {
                storagePermissionLauncher.launch(Unit)
            }
        } else checkSpecialPermissions()
    }

    private val requestDrawOverlaysDialog = object : DialogController() {
        override val properties: DialogProperties = DialogProperties(
            dismissOnClickOutside = false,
            dismissOnBackPress = false,
        )

        override fun onPositiveClick() {
            showState = false
            drawOverlaysSettingsLauncher.launchCanDrawOverlaysSettings(packageName)
        }

        @Composable
        fun Dialog() {
            AlertDialog(
                title = stringResource(R.string.text_required_floating_window_permission),
                content = stringResource(R.string.text_required_floating_window_permission),
                positiveText = stringResource(R.string.text_to_open),
                negativeText = stringResource(R.string.text_cancel),
                onNegativeClick = { finish() }
            )
        }
    }

    private val requestBackgroundStartDialog = object : DialogController() {
        override val properties: DialogProperties = DialogProperties(
            dismissOnClickOutside = false,
            dismissOnBackPress = false
        )

        @Composable
        fun Dialog() {
            AlertDialog(
                title = stringResource(R.string.text_requires_background_start),
                content = stringResource(R.string.text_requires_background_start_desc),
                positiveText = stringResource(R.string.text_to_open),
                onPositiveClick = {
                    showState = false
                    backgroundStartSettingsLauncher.launchAppPermissionsSettings(packageName)
                },
                negativeText = stringResource(R.string.text_cancel),
                onNegativeClick = { finish() }
            )
        }
    }

    private val requestAccessibilityServiceDialog = object : DialogController() {
        override val properties = DialogProperties(
            dismissOnClickOutside = false,
            dismissOnBackPress = false
        )

        override fun show() {
            lifecycleScope.launch {
                val enabled = withContext(Dispatchers.IO) {
                    AccessibilityServiceTool1.enableAccessibilityServiceByRootAndWaitFor(2000)
                }
                if (enabled) {
                    permissionsResult[Permissions.ACCESSIBILITY_SERVICES] = true
                    toast(this@SplashActivity, R.string.text_accessibility_service_turned_on)
                    checkSpecialPermissions()
                    return@launch
                }
                super.show()
            }
        }

        @Composable
        fun Dialog() {
            AlertDialog(
                title = stringResource(R.string.text_need_to_enable_accessibility_service),
                content = stringResource(
                    R.string.explain_accessibility_permission,
                    GlobalAppContext.appName
                ),
                positiveText = stringResource(R.string.text_to_open),
                onPositiveClick = {
                    showState = false
                    accessibilitySettingsLauncher.launch(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                },
                negativeText = stringResource(R.string.text_cancel),
                onNegativeClick = { finish() }
            )
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


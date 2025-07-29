package com.stardust.auojs.inrt

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.core.app.NotificationManagerCompat
import com.aiselp.autox.ui.material3.components.BaseDialog
import com.aiselp.autox.ui.material3.components.DialogController
import com.aiselp.autox.ui.material3.components.DialogTitle
import com.stardust.app.permission.BackgroundStartPermission
import com.stardust.app.permission.DrawOverlaysPermission
import com.stardust.app.permission.DrawOverlaysPermission.launchCanDrawOverlaysSettings
import com.stardust.app.permission.PermissionsSettingsUtil.launchAppPermissionsSettings
import com.stardust.auojs.inrt.autojs.AccessibilityServiceTool
import com.stardust.autojs.project.Constant
import com.stardust.autojs.util.PermissionUtil
import com.stardust.autojs.util.StoragePermissionResultContract
import org.autojs.autoxjs.inrt.R

class PermissionCheck : DialogController() {
    override val properties: DialogProperties =
        DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    private var permissions by mutableStateOf(mapOf<String, Boolean>())
    private var cal = {}

    override fun onPositiveClick() {
        dismiss()
        cal()
    }

    @Composable
    fun Dialog() {
        val context = LocalContext.current
        val t = remember {
            var i = 0
            permissions.forEach { (_, u) -> if (u) i++ }
            mutableIntStateOf(i)
        }
        val all: Boolean = t.intValue >= permissions.size
        BaseDialog(
            onDismissRequest = { dismiss() },
            title = { DialogTitle(stringResource(R.string.text_permission_prompt)) },
            positiveText = if (all) stringResource(R.string.ok) else null,
            negativeText = stringResource(R.string.exit),
            onNegativeClick = { (context as Activity).finish() },
        ) {
            val modifier = Modifier
            Column {
                Text(stringResource(R.string.text_permission_prompt_content))
                Spacer(Modifier.height(12.dp))
                for (e in permissions) {
                    var enabled by remember { mutableStateOf(e.value) }
                    LaunchedEffect(enabled) {
                        if (enabled) t.intValue++
                    }
                    when (e.key) {
                        Constant.Permissions.ACCESSIBILITY_SERVICES -> {
                            val activityResult =
                                rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                                    enabled = checkPermission(context, e.key)
                                }
                            Item(
                                modifier,
                                stringResource(R.string.accessibility_service),
                                enabled
                            ) { activityResult.launch(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)) }
                        }

                        Constant.Permissions.BACKGROUND_START -> {
                            val activityResult =
                                rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                                    enabled = checkPermission(context, e.key)
                                }
                            Item(
                                modifier,
                                stringResource(R.string.background_window_permission),
                                enabled
                            ) { activityResult.launchAppPermissionsSettings(context.packageName) }
                        }

                        Constant.Permissions.EXTERNAL_STORAGE -> {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                val activityResult =
                                    rememberLauncherForActivityResult(
                                        StoragePermissionResultContract()
                                    ) {
                                        enabled = checkPermission(context, e.key)
                                    }
                                Item(
                                    modifier,
                                    stringResource(R.string.text_file_manager_permission),
                                    enabled
                                ) { activityResult.launch(Unit) }
                            }
                        }

                        Constant.Permissions.DRAW_OVERLAY -> {
                            val activityResult =
                                rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                                    enabled = checkPermission(context, e.key)
                                }
                            Item(
                                modifier,
                                stringResource(R.string.draw_overlay_permission),
                                enabled
                            ) {
                                activityResult.launchCanDrawOverlaysSettings(context.packageName)
                            }
                        }

                        Constant.Permissions.PUBLISH_NOTIFICATION -> {
                            val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                                putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                            }
                            val activityResultLauncher =
                                rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                                    enabled = checkPermission(context, e.key)
                                }
                            Item(
                                modifier,
                                stringResource(R.string.text_publish_notification_permission),
                                enabled
                            ) {
                                activityResultLauncher.launch(intent)
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun Item(
        modifier: Modifier = Modifier,
        title: String,
        open: Boolean,
        request: () -> Unit
    ) {
        val m = if (open) modifier else modifier.clickable { request() }
        Row(
            modifier = m
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (open) {
                Icon(
                    imageVector = Icons.Default.Check,
                    tint = Color(0xFF4CAF50),
                    contentDescription = null,
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Warning,
                    tint = Color(0xFF9E9E9E),
                    contentDescription = null,
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(title, modifier = Modifier.weight(1f))
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                contentDescription = null
            )
        }
    }

    fun requestPermission(context: Context, permissionList: List<String>, onSuccess: () -> Unit) {
        val map = mutableMapOf<String, Boolean>()
        for (permission: String in permissionList) {
            map[permission] = checkPermission(context, permission)
        }
        permissions = map
        cal = onSuccess
        show()
    }

    fun checkPermission(context: Context, permissionList: List<String>): Boolean {
        for (permission: String in permissionList) {
            if (!checkPermission(context, permission)) {
                return false
            }
        }
        return true
    }

    fun checkPermission(context: Context, name: String): Boolean {
        return when (name) {
            Constant.Permissions.ACCESSIBILITY_SERVICES -> {
                return AccessibilityServiceTool.isAccessibilityServiceEnabled(context)
            }

            Constant.Permissions.BACKGROUND_START -> {
                return BackgroundStartPermission.isBackgroundStartAllowed(context)
            }

            Constant.Permissions.DRAW_OVERLAY -> {
                return DrawOverlaysPermission.isCanDrawOverlays(context)
            }

            Constant.Permissions.EXTERNAL_STORAGE -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !PermissionUtil.checkStoragePermission()) {
                    PermissionUtil.checkStoragePermission()
                } else true
            }

            Constant.Permissions.PUBLISH_NOTIFICATION -> {
                NotificationManagerCompat.from(context).areNotificationsEnabled()
            }

            else -> false
        }
    }
}
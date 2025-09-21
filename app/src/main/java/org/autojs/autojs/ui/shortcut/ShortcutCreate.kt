package org.autojs.autojs.ui.shortcut

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Icon
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.lifecycleScope
import com.aiselp.autox.ui.material3.components.BaseDialog
import com.aiselp.autox.ui.material3.components.ComposeDialog
import com.aiselp.autox.ui.material3.components.DialogTitle
import com.aiselp.autox.utils.setupStartIntent
import com.stardust.autojs.execution.ScriptExecuteActivity
import com.stardust.autojs.script.JavaScriptSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.autojs.autojs.external.shortcut.ShortcutManager
import org.autojs.autojs.model.script.ScriptFile
import org.autojs.autojs.ui.log.LogActivityKt
import org.autojs.autojs.ui.shortcut.ShortcutCreate.ACTION_START_SCRIPT
import org.autojs.autoxjs.R


object ShortcutCreate {
    const val ACTION_START_SCRIPT = "Action_Start_Script"
}


fun ShortcutCreate.showDialog(activity: ComponentActivity, file: ScriptFile) {
    ComposeDialog(activity) {
        var icon: Bitmap? by remember { mutableStateOf(null) }
        val launcher =
            rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode != RESULT_OK || it.data == null) return@rememberLauncherForActivityResult
                activity.lifecycleScope.launch {
                    icon = createIcon(it.data!!, activity)
                }
            }
        BaseDialog(
            title = { DialogTitle(stringResource(R.string.text_send_shortcut)) },
            positiveText = stringResource(R.string.ok),
            onPositiveClick = {
                createShortcutByShortcutManager(activity, file, icon)
                dismiss()
            },
            negativeText = stringResource(R.string.cancel),
            onNegativeClick = { dismiss() }
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.border(
                        BorderStroke(1.dp, Color(0xFFDCC564)),
                        RoundedCornerShape(12.dp)
                    )
                ) {
                    IconButton(
                        onClick = {
                            launcher.launch(
                                Intent(activity, ShortcutIconSelectActivity::class.java)
                            )
                        }) {
                        if (icon == null) {
                            Icon(
                                painter = painterResource(R.drawable.ic_add_white_48dp),
                                contentDescription = null
                            )
                        } else Image(
                            bitmap = icon!!.asImageBitmap(),
                            contentDescription = null
                        )
                    }
                }
                Spacer(Modifier.width(6.dp))

                var v by remember { mutableStateOf(file.simplifiedName) }
                OutlinedTextField(
                    value = v,
                    onValueChange = { v = it },
                    singleLine = true,
                    label = { Text(stringResource(R.string.text_name)) }
                )
            }
        }
    }.show()

}

private suspend fun createIcon(intent: Intent, context: Context): Bitmap? {
    val packageName =
        intent.getStringExtra(ShortcutIconSelectActivity.EXTRA_PACKAGE_NAME)
    if (packageName != null) {
        try {
            return withContext(Dispatchers.IO) {
                context.packageManager.getApplicationIcon(packageName).toBitmap()
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            return null
        }
    }
    val uri = intent.data ?: return null
    return withContext(Dispatchers.IO) {
        BitmapFactory.decodeStream(context.contentResolver.openInputStream(uri))
    }
}

private fun createShortcutByShortcutManager(
    context: Context,
    file: ScriptFile,
    bitmap: Bitmap?
) {
    val icon = if (bitmap == null) {
        Icon.createWithResource(context, R.drawable.ic_file_type_js)
    } else {
        Icon.createWithBitmap(bitmap)
    }
    val source = file.toSource()
    val intent =
        if (source is JavaScriptSource && source.executionMode and JavaScriptSource.EXECUTION_MODE_UI != 0) {
            val intent = Intent(context, ScriptExecuteActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        } else {
            Intent(context, LogActivityKt::class.java)
        }
    intent.setAction(ACTION_START_SCRIPT)
    setupStartIntent(intent, source, null)
    ShortcutManager.getInstance(context)
        .addPinnedShortcut(file.simplifiedName, file.path, icon, intent)
}
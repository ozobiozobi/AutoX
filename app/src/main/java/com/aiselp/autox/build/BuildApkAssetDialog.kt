package com.aiselp.autox.build

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.DialogProperties
import com.aiselp.autox.ui.material3.components.BaseDialog
import com.aiselp.autox.ui.material3.components.DialogController
import com.aiselp.autox.ui.material3.components.DialogTitle
import com.stardust.toast
import com.stardust.util.IntentUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.autojs.autojs.build.ApkBuilderPluginHelper.checkTemplateApkAsset
import org.autojs.autojs.build.ApkBuilderPluginHelper.setTemplateApkAsset
import org.autojs.autoxjs.R

class BuildApkAssetDialog : DialogController() {
    override val properties: DialogProperties =
        DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)

    @Composable
    fun Dialog() {
        val context = LocalContext.current
        val scope = rememberCoroutineScope()
        val dialog = DialogController(properties)
        val a = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) {
            if (it != null) {
                dismiss()
                dialog.show()
                scope.launch(Dispatchers.IO) {
                    val s = try {
                        setTemplateApkAsset(context, context.contentResolver.openInputStream(it)!!)
                        true
                    } catch (e: Exception) {
                        false
                    }
                    withContext(Dispatchers.Main) {
                        dialog.dismiss()
                        if (s) {
                            toast(context, R.string.text_import_succeed)
                        } else {
                            show()
                            toast(context, R.string.text_import_fail)
                        }

                    }
                }
            }
        }
        LaunchedEffect(Unit) {
            if (!checkTemplateApkAsset(context)) {
                show()
            }
        }
        dialog.BaseDialog(
            title = {
                DialogTitle(stringResource(R.string.text_on_progress))
            },
            onDismissRequest = {},
        ) {
            CircularProgressIndicator()
        }
        BaseDialog(
            onDismissRequest = { dismiss() },
            title = { DialogTitle(stringResource(R.string.asset_download)) },
            positiveText = stringResource(R.string.go_download),
            onPositiveClick = { IntentUtil.browse(context, templateApkDownloadUrl) },
            negativeText = stringResource(R.string.exit),
            onNegativeClick = {
                dismiss()
                if (context is Activity) context.finish()
            },
            neutralText = stringResource(R.string.import_asset),
            onNeutralClick = { a.launch(arrayOf("application/vnd.android.package-archive")) },
        ) {
            Text(text = stringResource(R.string.import_template_apk_desc))
        }
    }

    companion object {
        private const val templateApkDownloadUrl = "https://github.com/aiselp/AutoX/releases/"
    }
}
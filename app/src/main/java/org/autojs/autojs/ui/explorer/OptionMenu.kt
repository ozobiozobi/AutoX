package org.autojs.autojs.ui.explorer

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import org.autojs.autojs.model.explorer.ExplorerItem
import org.autojs.autojs.model.explorer.ExplorerSampleItem
import org.autojs.autoxjs.R


fun ExplorerItem.createOptionMenu(): MutableList<OptionMenu> {
    val menus = mutableListOf<OptionMenu>()
    if (isExecutable) {
        menus.add(OptionMenu.RUN_REPEATEDLY)
        menus.add(OptionMenu.TIMED_TASK)
        menus.add(OptionMenu.CREATE_SHORTCUT)
        menus.add(OptionMenu.OPEN_BY_OTHER_APPS)
        menus.add(OptionMenu.BUILD_APK)
    }
    if (canDelete()) {
        menus.add(OptionMenu.DELETE)
    }
    if (canRename()) {
        menus.add(OptionMenu.RENAME)
    }
    if (this is ExplorerSampleItem) {
        menus.add(OptionMenu.RESET_TO_INITIAL_CONTENT)
    }
    return menus
}

@Composable
fun ExplorerViewKt.OptionMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    menus: List<OptionMenu>,
    onMenuSelect: (OptionMenu) -> Unit
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest
    ) {
        menus.forEach {
            DropdownMenuItem(
                text = { Text(text = stringResource(it.resId)) },
                onClick = { onMenuSelect(it) }
            )
        }
    }
}

@Composable
fun OptionMenu2(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    menus: List<OptionMenu2>,
    onMenuSelect: (OptionMenu2) -> Unit
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest
    ) {
        menus.forEach {
            DropdownMenuItem(
                text = { Text(text = stringResource(it.resId)) },
                onClick = { onMenuSelect(it) }
            )
        }
    }
}

enum class OptionMenu(val resId: Int) {
    RUN_REPEATEDLY(R.string.text_run_repeatedly),
    RENAME(R.string.text_rename),
    DELETE(R.string.text_delete),
    SEND(R.string.text_send),
    RESET_TO_INITIAL_CONTENT(R.string.text_reset_to_initial_content),

    TIMED_TASK(R.string.text_timed_task),
    CREATE_SHORTCUT(R.string.text_send_shortcut),
    OPEN_BY_OTHER_APPS(R.string.text_open_by_other_apps),
    BUILD_APK(R.string.text_build_apk),

}

enum class OptionMenu2(val resId: Int) {
    NAME(R.string.text_name),
    TIME(R.string.text_time),
    SIZE(R.string.text_size),
    TYPE(R.string.text_type),
}
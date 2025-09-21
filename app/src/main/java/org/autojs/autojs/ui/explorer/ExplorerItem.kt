package org.autojs.autojs.ui.explorer

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.autojs.autojs.ui.filechooser.FileChooseListView
import org.autojs.autoxjs.R

@Composable
private fun FileIcon(text: String, background: Color) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(32.dp))
            .background(background),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            textAlign = TextAlign.Center,
            color = Color(0xFFFFFFFF),
            fontSize = 24.sp
        )
    }
}

@Composable
fun FileInfo(modifier: Modifier = Modifier, name: String, desc: String) {
    Column(modifier = modifier) {
        Text(
            text = name,
            modifier = Modifier.fillMaxWidth(),
            maxLines = 1,
            fontSize = 14.sp
        )
        Text(
            text = desc,
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.secondary,
            maxLines = 1,
            fontSize = 11.sp
        )
    }
}

@Composable
fun ExplorerItem(
    item: ExplorerViewKt.ExplorerItemViewHolder,
    optionMenuContent: @Composable () -> Unit,
) {
    val config = LocalExplorerItemConfig.current
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 9.dp)
            .clickable { item.onItemClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        FileIcon(item.firstChar, item.firstCharBackground)
        Spacer(Modifier.width(16.dp))
        FileInfo(modifier = Modifier.weight(1f), name = item.name, desc = item.desc)
        Row(verticalAlignment = Alignment.CenterVertically) {
            val tint = Color(0xFFA9AAAB)
            val modifier = Modifier
                .height(40.dp)
                .width(35.dp)
                .clip(RoundedCornerShape(32.dp))
            val iconModifier = Modifier.size(18.dp)
            if (item.runVisibility && config.showRun) Box(
                modifier.clickable { item.run() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    modifier = iconModifier,
                    painter = painterResource(R.drawable.ic_run_gray),
                    contentDescription = null,
                    tint = tint
                )
            }
            if (item.editVisibility && config.showEdit) Box(
                modifier.clickable { item.edit() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    modifier = iconModifier,
                    painter = painterResource(R.drawable.ic_mode_edit_black_24dp),
                    contentDescription = null,
                    tint = tint
                )
            }
            if (config.showMore) Box(
                modifier.clickable { item.showMenu = true },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    modifier = iconModifier,
                    painter = painterResource(R.drawable.ic_more_vert_black_24dp),
                    contentDescription = null,
                    tint = tint
                )
            }
            optionMenuContent()
        }
    }
}

@Composable
fun FileChooseExplorerItem(item: FileChooseListView.ExplorerItemViewHolder) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 9.dp)
            .clickable { item.onCheckedChanged() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        FileIcon(item.firstChar, item.firstCharBackground)
        Spacer(Modifier.width(16.dp))
        FileInfo(modifier = Modifier.weight(1f), name = item.name, desc = item.desc)
        Checkbox(checked = item.checked, onCheckedChange = { item.onCheckedChanged() })
    }
}

@Composable
private fun IconBox(modifier: Modifier, content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .padding(horizontal = 4.dp)
            .size(24.dp)
            .then(modifier),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

@Composable
fun CategoryItem(holder: ExplorerViewKt.CategoryViewHolder, options: @Composable () -> Unit) {
    val tintColor = Color(0xFF828384)

    Row(
        modifier = Modifier.clickable { holder.collapseOrExpand() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        val state by animateFloatAsState(if (holder.arrowCollapsed) -90f else 0f)
        IconBox(
            modifier = Modifier.rotate(state),
        ) {
            Icon(
                modifier = Modifier.size(20.dp),
                painter = painterResource(R.drawable.ic_expanded),
                contentDescription = null,
                tint = tintColor
            )
        }

        Text(
            text = holder.title,
            modifier = Modifier.weight(1f),
            color = tintColor,
            fontSize = 12.sp
        )

        IconBox(Modifier.clickable { holder.goBack2() }) {
            Icon(
                modifier = Modifier.size(16.dp),
                painter = painterResource(R.drawable.ic_dir_up),
                contentDescription = null,
                tint = tintColor
            )
        }
        IconBox(Modifier.clickable { holder.changeSortOrder() }) {
            Icon(
                modifier = Modifier.size(16.dp),
                painter = painterResource(
                    if (holder.sortOrder) R.drawable.ic_ascending_order
                    else R.drawable.ic_descending_order
                ),
                contentDescription = null,
                tint = tintColor
            )
        }

        IconBox(Modifier.clickable { holder.showMenu = true }) {
            options()
            Icon(
                modifier = Modifier.size(20.dp),
                painter = painterResource(R.drawable.ic_sort),
                contentDescription = null,
                tint = tintColor
            )
        }
    }
}


data class ExplorerItemConfig(
    val showRun: Boolean = true,
    val showEdit: Boolean = true,
    val showMore: Boolean = true
)

val LocalExplorerItemConfig = staticCompositionLocalOf { ExplorerItemConfig() }
package org.autojs.autojs.ui.filechooser

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.stardust.pio.PFile
import org.autojs.autojs.model.explorer.ExplorerItem
import org.autojs.autojs.model.explorer.ExplorerPage
import org.autojs.autojs.model.script.ScriptFile
import org.autojs.autojs.ui.explorer.ExplorerViewKt
import org.autojs.autojs.ui.explorer.FileChooseExplorerItem
import org.autojs.autojs.ui.widget.BindableViewHolder

/**
 * Created by Stardust on 2017/10/19.
 */
class FileChooseListView : ExplorerViewKt {
    private var mMaxChoice = 1
    private val mSelectedFiles = LinkedHashMap<PFile, Int>()
    private var mCanChooseDir = false
    val selectedFiles: List<PFile>
        get() {
            return mSelectedFiles.keys.toList()
        }

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    fun setMaxChoice(maxChoice: Int) {
        mMaxChoice = maxChoice
    }

    fun setCanChooseDir(canChooseDir: Boolean) {
        mCanChooseDir = canChooseDir
    }

    private fun init() {
        (explorerItemListView.itemAnimator as SimpleItemAnimator?)?.supportsChangeAnimations =
            false
    }

    override fun onCreateViewHolder(
        inflater: LayoutInflater,
        parent: ViewGroup?,
        viewType: Int
    ): BindableViewHolder<Any> {
        return when (viewType) {
            VIEW_TYPE_ITEM -> ExplorerItemViewHolder(ComposeView(context))
            VIEW_TYPE_PAGE -> ExplorerPageViewHolder(ComposeView(context))
            else -> super.onCreateViewHolder(inflater, parent, viewType)
        }
    }

    private fun check(file: ScriptFile, position: Int) {
        if (mSelectedFiles.size == mMaxChoice) {
            val (key, positionOfItemToUncheck) = mSelectedFiles.entries.iterator().next()
            mSelectedFiles.remove(key)
            explorerItemListView.adapter!!.notifyItemChanged(positionOfItemToUncheck)
        }
        mSelectedFiles[file] = position
    }

    inner class ExplorerItemViewHolder(view: ComposeView) :
        ExplorerViewKt.ExplorerItemViewHolder(view) {
        private var mExplorerItem: ExplorerItem? = null
        var checked by mutableStateOf(false)

        init {
            view.setContent {
                FileChooseExplorerItem(this)
            }
        }

        override fun bind(item: Any, position: Int) {
            super.bind(item, position)
            if (item !is ExplorerItem) return
            mExplorerItem = item
            checked = mSelectedFiles.containsKey(item.toScriptFile())
        }

        fun onCheckedChanged() {
            checked = !checked
            if (checked) {
                check(mExplorerItem!!.toScriptFile(), absoluteAdapterPosition)
            } else {
                mSelectedFiles.remove(mExplorerItem!!.toScriptFile())
            }
        }
    }

    inner class ExplorerPageViewHolder(view: ComposeView) :
        ExplorerViewKt.ExplorerPageViewHolder(view) {
        var checked by mutableStateOf(false)
        val showCheckBox = mCanChooseDir

        private var mExplorerPage: ExplorerPage? = null
        override fun bind(data: Any, position: Int) {
            super.bind(data, position)
            if (data !is ExplorerPage) return
            mExplorerPage = data
            if (mCanChooseDir) {
                checked = mSelectedFiles.containsKey(data.toScriptFile())
            }
        }

        fun onCheckedChanged() {
            checked = !checked
            if (checked) {
                check(mExplorerPage!!.toScriptFile(), absoluteAdapterPosition)
            } else {
                mSelectedFiles.remove(mExplorerPage!!.toScriptFile())
            }
        }
    }
}
package xyz.piscesxp.pajtools.ui.record.list

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_record_list_item.view.*
import kotlinx.android.synthetic.main.record_menu.view.*

import xyz.piscesxp.pajtools.ui.record.list.RecordListFragment.RecordListFragmentListeners
import xyz.piscesxp.pajtools.dummy.DummyContent.DummyItem

import xyz.piscesxp.pajtools.R
import xyz.piscesxp.pajtools.data.record.RecordData
import java.text.SimpleDateFormat
import java.util.*


class RecordListItemRecyclerViewAdapter(
    private var mValues: List<RecordData>,
    private val fragmentListeners: RecordListFragmentListeners?,
    private val myListeners: RecordListItemRecyclerViewAdapter.Listeners
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val mOnClickListener: View.OnClickListener
    private val TAG = this.javaClass.canonicalName

    enum class VIEW_TYPE(val type: Int) {
        ITEM_TYPE_MENU(0),
        ITEM_TYPE_RECORD(1)
    }

    init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as DummyItem
            // Notify the active callbacks interface (the activity, if the fragment is attached to
            // one) that an item has been selected.
            //fragmentListeners?.onBackupButtonPressed(item)
        }
    }

    /**
     * 更新数据
     * */
    fun updateDataSet(newValues: List<RecordData>) {
        mValues = newValues
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            VIEW_TYPE.ITEM_TYPE_MENU.type ->
                return MenuViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.record_menu, parent, false)
                )
            VIEW_TYPE.ITEM_TYPE_RECORD.type ->
                return ItemViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.fragment_record_list_item, parent, false)
                )
            else -> {
                return ItemViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.fragment_record_list_item, parent, false)
                )
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> VIEW_TYPE.ITEM_TYPE_MENU.type
            else -> VIEW_TYPE.ITEM_TYPE_RECORD.type
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MenuViewHolder) {
            //menu
            holder.switchAccount.setOnClickListener(View.OnClickListener {
                val gameAccountDataList = myListeners.onChangeAccount()
            }

        } else if (holder is ItemViewHolder) {
            //record item
            val item = mValues[position - 1]
            //日期
            val date = Date((item.endTime).toLong() * 1000)
            holder.matchTime.text = SimpleDateFormat("yyyy-MM-dd kk:mm").format(date)
            //模式
            holder.matchType.text = when (item.matchID) {
                10 -> "资质赛"
                21 -> "逢魔之战"
                2 -> "为崽而战"
                else -> {
                    Log.d(TAG, "Unknown match type ${item.matchID}")
                    "未知模式"
                }
            }
            //备份
            val backup = true
            if (backup) {
                holder.let {
                    it.backupButton.visibility = View.INVISIBLE
                    it.successPrompt.visibility = View.VISIBLE
                    it.stateIcon.visibility = View.VISIBLE
                }
            } else {
                holder.let {
                    it.backupButton.visibility = View.VISIBLE
                    it.successPrompt.visibility = View.INVISIBLE
                    it.stateIcon.visibility = View.INVISIBLE
                    it.backupButton.setOnClickListener { v ->
                        holder.successPrompt.text = "备份成功"
                        fragmentListeners?.onBackupButtonPressed(item)
                    }
                }
            }
            //获胜
            var victory: String
            var victoryTextColor: Int
            if (item.result) {
                victory = "胜利 "
                victoryTextColor = Color.CYAN
            } else {
                victory = "失败 "
                victoryTextColor = Color.RED
            }
            holder.victory.let {
                it.text = victory
                it.setTextColor(victoryTextColor)
            }

            //加载式神图片
            val heroID: Int? = item.heroData[item.pos.toString()]?.heroID
            if (heroID != null) {
                val url = fragmentListeners?.onLoadHeroImage(heroID)
                Log.d(TAG, "loading image $url")
                Picasso.get().load(url).into(holder.heroImage)
            }
        }
    }

    override fun getItemCount(): Int = mValues.size + 1

    inner class ItemViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val matchType: TextView = mView.matchType
        val matchTime: TextView = mView.matchTime
        val backupButton: Button = mView.backupButton
        val successPrompt: TextView = mView.successPrompt
        val heroImage: ImageView = mView.heroImage
        val victory: TextView = mView.victory
        val stateIcon: ImageView = mView.stateIcon
    }

    inner class MenuViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val accountName: TextView = mView.accountInfo
        val switchAccount: Button = mView.switchAccount
        val inGameRecord: TextView = mView.inGameRecordCount
        val startBackup: Button = mView.startBackup
        val alreadyBackupRecord: TextView = mView.backupRecordCount
        val restoreBackup: Button = mView.restoreBackup

    }

    interface Listeners {
        fun onChangeAccount()
        fun onDataSetChange()
    }
}

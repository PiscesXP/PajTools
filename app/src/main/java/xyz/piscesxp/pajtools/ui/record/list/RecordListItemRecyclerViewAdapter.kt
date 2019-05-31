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
import org.w3c.dom.Text


import xyz.piscesxp.pajtools.ui.record.list.RecordListFragment.RecordListFragmentListeners
import xyz.piscesxp.pajtools.dummy.DummyContent.DummyItem

import xyz.piscesxp.pajtools.R
import xyz.piscesxp.pajtools.data.record.RecordData
import java.text.SimpleDateFormat
import java.util.*

/**
 * [RecyclerView.Adapter] that can display a [DummyItem] and makes a call to the
 * specified [RecordListFragmentListeners].
 * TODO: Replace the implementation with code for your data type.
 */
class RecordListItemRecyclerViewAdapter(
    private val mValues: List<RecordData>,
    private val mListenerRecord: RecordListFragmentListeners?
) : RecyclerView.Adapter<RecordListItemRecyclerViewAdapter.ViewHolder>() {

    private val mOnClickListener: View.OnClickListener
    private val TAG = this.javaClass.canonicalName

    init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as DummyItem
            // Notify the active callbacks interface (the activity, if the fragment is attached to
            // one) that an item has been selected.
            //mListenerRecord?.onBackupButtonPressed(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_record_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]
        //日期
        val date = Date((item.endTime).toLong() * 1000)
        holder.mMatchTime.text = SimpleDateFormat("yyyy-MM-dd kk:mm").format(date)
        //模式
        holder.mMatchType.text = when (item.matchID) {
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
                it.mBackupButton.visibility = View.INVISIBLE
                it.mSuccessPrompt.visibility = View.VISIBLE
                it.mStateIcon.visibility = View.VISIBLE
            }
        } else {
            holder.let {
                it.mBackupButton.visibility = View.VISIBLE
                it.mSuccessPrompt.visibility = View.INVISIBLE
                it.mStateIcon.visibility = View.INVISIBLE
                it.mBackupButton.setOnClickListener { v ->
                    holder.mSuccessPrompt.text = "备份成功"
                    mListenerRecord?.onBackupButtonPressed(item)
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
        holder.mVictory.let {
            it.text = victory
            it.setTextColor(victoryTextColor)
        }

        //加载式神图片
        val heroID: Int? = item.heroData[item.pos.toString()]?.heroID
        if (heroID != null) {
            val url = mListenerRecord?.onLoadHeroImage(heroID)
            Log.d(TAG, "loading image $url")
            Picasso.get().load(url).into(holder.mHeroImage)
        }
    }

    override fun getItemCount(): Int = mValues.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mMatchType: TextView = mView.matchType
        val mMatchTime: TextView = mView.matchTime
        val mBackupButton: Button = mView.backupButton
        val mSuccessPrompt: TextView = mView.successPrompt
        val mHeroImage: ImageView = mView.heroImage
        val mVictory: TextView = mView.victory
        val mStateIcon: ImageView = mView.stateIcon
    }
}

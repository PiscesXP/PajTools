package xyz.piscesxp.pajtools.ui.record.list

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import xyz.piscesxp.pajtools.R
import xyz.piscesxp.pajtools.data.account.GameAccountData
import xyz.piscesxp.pajtools.data.database.CombatRecordEntity
import xyz.piscesxp.pajtools.data.record.RecordData
import xyz.piscesxp.pajtools.utility.PermissionChecker

import java.io.File
import kotlin.random.Random
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import com.google.android.material.snackbar.Snackbar
import xyz.piscesxp.pajtools.data.account.Record


class RecordListFragment : Fragment(), RecordListItemRecyclerViewAdapter.Listeners {


    private var myListeners: RecordListFragmentListeners? = null
    private var mAdapter: RecordListItemRecyclerViewAdapter? = null
    private val TAG = this.javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_item_list, container, false)

        if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            requestPermissions(Array(1) { Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0)
            "shit"
        } else {
            selectAccount()
        }

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is RecordListFragmentListeners) {
            myListeners = context
        } else {
            throw RuntimeException(context.toString() + " must implement RecordListFragmentListeners")
        }
    }

    override fun onDetach() {
        super.onDetach()
        myListeners = null
    }


    //communicate interface
    interface RecordListFragmentListeners {
        fun onBackupButtonPressed(item: RecordData)
        fun onRecordSelected(item: RecordData)
        fun onLoadHeroImage(heroID: Int): String?
        /**
         * @return All account data list.
         * */
        fun onRequireAllData(): List<GameAccountData>

    }

    fun setRecordListFragmentListeners(listeners: RecordListFragmentListeners) {
        myListeners = listeners
    }

    fun updateAdapterDataSet(newDataSet: GameAccountData) {
        //转换
        val recyclerView = view?.findViewById<RecyclerView>(R.id.item_list)
        if (recyclerView is RecyclerView) {
            if (mAdapter == null) {
                recyclerView.adapter =
                    RecordListItemRecyclerViewAdapter(newDataSet, myListeners, this@RecordListFragment)
            } else {
                mAdapter?.updateDataSet(newDataSet)
            }
            Toast.makeText(requireContext(), "数据更新成功", Toast.LENGTH_SHORT).show()
        } else {
            Log.e(TAG, "RecyclerView not found!")
        }
    }

    private fun selectAccount() {
        var items = mutableListOf<String>()
        val gameAccountDataList = myListeners?.onRequireAllData()
        if (gameAccountDataList == null) return
        Log.d("shit", "${gameAccountDataList?.size}")
        for (account in gameAccountDataList) {
            items.add("${account.gameSourceType.sourceName} - ${account.accountName}")
        }

        val singleChoiceDialog = AlertDialog.Builder(requireContext())
        var choice: Int = 0
        singleChoiceDialog.setTitle("选择要备份的账号")
        singleChoiceDialog.setSingleChoiceItems(
            items.toTypedArray(),
            choice,
            { dialog, which -> choice = which })
        singleChoiceDialog.setPositiveButton(
            "确定",
            { _, which ->
                if (gameAccountDataList != null) {
                    updateAdapterDataSet(gameAccountDataList[choice])
                } else {
                    //TODO
                }
            })
        singleChoiceDialog.show()
    }

    override fun onChangeAccount() {
        selectAccount()
    }

    override fun onDataSetChange() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            RecordListFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }


    private fun readCombatRecords(): List<RecordData> {
        val path =
            "${Environment.getExternalStorageDirectory().absolutePath}/Android/data/com.netease.moba.mi/files/Netease/moba/Documents/LocalCombat"
        val combatDescriptionFilePattern = "^.*\\.meta$"
        val resultList = mutableListOf<RecordData>()

        val rootDir = File(path)
        if (rootDir.exists()) {
            //game dir
            for (subDir in rootDir.listFiles()) {
                //player dir
                for (file in subDir.listFiles()) {
                    //combat file
                    val fileName = file.name
                    if (fileName.matches(Regex(combatDescriptionFilePattern))) {
                        //is description file
                        //read player name
                        val recordData = Gson().fromJson<RecordData>(
                            file.inputStream().bufferedReader().readText(),
                            RecordData::class.java
                        )
                        resultList.add(recordData)
                    }
                }
            }
        }
        return resultList
    }
}

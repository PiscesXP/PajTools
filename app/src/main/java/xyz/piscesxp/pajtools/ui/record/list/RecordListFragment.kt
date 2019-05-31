package xyz.piscesxp.pajtools.ui.record.list

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.gson.Gson
import xyz.piscesxp.pajtools.R
import xyz.piscesxp.pajtools.data.database.AppDatabase
import xyz.piscesxp.pajtools.data.database.CombatRecordDao
import xyz.piscesxp.pajtools.data.database.CombatRecordEntity
import xyz.piscesxp.pajtools.data.record.RecordData
import xyz.piscesxp.pajtools.utility.PermissionChecker

import java.io.File
import kotlin.random.Random

/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [RecordListFragment.RecordListFragmentListeners] interface.
 */
class RecordListFragment : Fragment() {

    // TODO: Customize parameters
    private var columnCount = 1

    private var myListeners: RecordListFragmentListeners? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_item_list, container, false)


        //申请权限
        if (!PermissionChecker.checkPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            //没权限，向用户申请
            Toast.makeText(requireContext(), "需要读取本机文件来获取记录", Toast.LENGTH_LONG).show()
            requestPermissions(Array(1) { Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0)
        }
        if (!PermissionChecker.checkPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(requireContext(), "未获得权限", Toast.LENGTH_LONG).show()
            return view
        }

        val recordDataList = readCombatRecords()
        for (recordData in recordDataList) {
            val combatRecordEntity = CombatRecordEntity(
                id = Random.nextInt(),
                isBackupLocal = false,
                isBackupRemote = false,
                recordData = recordData
            )
            //AppDatabase.getDatabase(requireContext())

        }
        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                adapter = RecordListItemRecyclerViewAdapter(readCombatRecords(), myListeners)
            }
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
    }

    fun setRecordListFragmentListeners(listeners: RecordListFragmentListeners) {
        myListeners = listeners
    }


    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
            RecordListFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
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

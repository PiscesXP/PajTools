package xyz.piscesxp.pajtools

import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import xyz.piscesxp.pajtools.data.HeroImage
import xyz.piscesxp.pajtools.data.account.GameAccountData
import xyz.piscesxp.pajtools.data.account.Record
import xyz.piscesxp.pajtools.data.record.RecordData
import xyz.piscesxp.pajtools.ui.record.list.RecordListFragment
import xyz.piscesxp.pajtools.ui.record.list.RecordListItemDetailFragment
import xyz.piscesxp.pajtools.utility.GameSourceType
import java.io.File
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog


class MainActivity : AppCompatActivity(), RecordListFragment.RecordListFragmentListeners,
    RecordListItemDetailFragment.RecordListItemDetailFragmentListeners {
    companion object {
        private val TAG = MainActivity::class.java.canonicalName
    }

    private lateinit var mRecordListFragment: RecordListFragment

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                val fragmentManager = supportFragmentManager
                fragmentManager.beginTransaction().replace(R.id.frameLayout, mRecordListFragment).commit()
                fragmentManager.addOnBackStackChangedListener(FragmentManager.OnBackStackChangedListener {
                    Log.d(
                        TAG,
                        "Fragment back"
                    )
                })
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)


        //switch to RecordListFragment
        mRecordListFragment = RecordListFragment.newInstance()
        supportFragmentManager.beginTransaction().add(R.id.frameLayout, mRecordListFragment).commit()
    }


    //--------------------------------------------------------------
    fun getAvaliableGameAccount(): List<GameAccountData> {
        val result = mutableListOf<GameAccountData>()
        val pathPrefix = "${Environment.getExternalStorageDirectory().absolutePath}/Android/data/"
        val pathPostfix = "/files/Netease/moba/Documents/LocalCombat"
        for (type in GameSourceType.values()) {
            //检查每一种渠道服
            val rootDir = File("${pathPrefix}${type.toPackageName()}${pathPostfix}")
            if (rootDir.exists()) {
                //游戏录像根目录
                Log.d(TAG, "Found game source type:${type.sourceName}")
                for (userDir in rootDir.listFiles().filter { dir -> dir.isDirectory() }) {
                    //玩家录像根目录
                    Log.d(TAG, "Found user dir:${userDir.name}")
                    val recordDataList = mutableListOf<RecordData>()
                    var accountName: String? = null
                    for (metaFile in userDir.listFiles().filter { file -> file.name.matches(Regex("^.*\\.meta$")) }) {
                        //录像meta文件
                        val recordData = Gson().fromJson<RecordData>(
                            metaFile.inputStream().bufferedReader().readText(),
                            RecordData::class.java
                        )
                        accountName = recordData.heroData[recordData.pos.toString()]?.name
                        recordDataList.add(recordData)
                    }
                    if (accountName == null) {
                        accountName = "未知用户"
                    }
                    val recordList = mutableListOf<Record>()
                    //TODO 检查是否已备份
                    recordDataList.forEach { recordData -> recordList.add(Record(recordData, true, false)) }
                    result.add(
                        GameAccountData(
                            gameSourceType = type,
                            accountName = accountName,
                            recordList = recordList
                        )
                    )
                }
            }
        }
        return result
    }

    //--------------------------------------------------------------
    //implements of fragment listeners
    override fun onBackupButtonPressed(item: RecordData) {
        Toast.makeText(applicationContext, "已备份文件${item.guid}", Toast.LENGTH_SHORT).show()
    }

    override fun onRecordSelected(item: RecordData) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        supportFragmentManager.beginTransaction().replace(R.id.frameLayout, RecordListItemDetailFragment()).commit()
    }

    override fun onLoadHeroImage(heroID: Int): String? {
        return HeroImage.getImageUrlByHeroID(heroID, assets)
    }

    override fun onLoadRecordData(recordData: RecordData) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /**
     * 根据目录判断渠道服、账号，提示框选择切换.
     * */
    override fun onRequireAllData():List<GameAccountData> {
        return getAvaliableGameAccount()
    }
}

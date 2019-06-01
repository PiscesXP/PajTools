package xyz.piscesxp.pajtools

import android.Manifest
import android.app.Activity
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
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


class MainActivity : AppCompatActivity(), RecordListFragment.RecordListFragmentListeners,
    RecordListItemDetailFragment.RecordListItemDetailFragmentListeners,
    ActivityCompat.OnRequestPermissionsResultCallback {
    companion object {
        private val TAG = MainActivity::class.java.canonicalName
    }

    private lateinit var mRecordListFragment: RecordListFragment
    private val mPermissionRequestCode: Int = 32

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

        checkPermission()
    }

    //--------------------------------------
    fun start() {
        //switch to RecordListFragment
        mRecordListFragment = RecordListFragment.newInstance()
        supportFragmentManager.beginTransaction().add(R.id.frameLayout, mRecordListFragment).commit()
    }

    /**
     * 检查权限，没有的话向用户申请
     * */
    fun checkPermission() {
        val requirePermissions = Array<String>(1) { Manifest.permission.WRITE_EXTERNAL_STORAGE }
        var isGranted = true
        for (permission in requirePermissions) {
            isGranted = isGranted && ActivityCompat.checkSelfPermission(
                applicationContext,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        }
        if (isGranted) {
            //已获得权限，开始
            Log.d(TAG, "Starting")
            start()
        } else {
            //向用户申请权限
            Log.d(TAG, "Acquiring permission")
            val activity: Activity = this
            with(AlertDialog.Builder(this)) {
                setTitle("APP需要以下权限")
                setMessage("- 存储权限: 用于读取录像文件列表 ^_^")
                setPositiveButton("好的", DialogInterface.OnClickListener { dialog, which ->
                    ActivityCompat.requestPermissions(activity, requirePermissions, mPermissionRequestCode)
                })
                show()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        checkPermission()
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
                Log.d(TAG, "Found game source type:${type.sourceName}, ${rootDir.absolutePath}")
                for (userDir in rootDir.listFiles().filter { file -> file.isDirectory() }) {
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
    override fun onRequireAllData(): List<GameAccountData> {
        return getAvaliableGameAccount()
    }


}

package xyz.piscesxp.pajtools

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import xyz.piscesxp.pajtools.data.HeroImage
import xyz.piscesxp.pajtools.data.record.RecordData
import xyz.piscesxp.pajtools.ui.record.list.RecordListFragment
import xyz.piscesxp.pajtools.utility.PermissionChecker
import java.io.File
import java.lang.StringBuilder

class MainActivity : AppCompatActivity(), RecordListFragment.RecordListFragmentListeners {
    companion object {
        private val TAG = MainActivity.javaClass.canonicalName
    }

    private lateinit var mRecordListFragment: RecordListFragment

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                supportFragmentManager.beginTransaction().replace(R.id.frameLayout, mRecordListFragment).commit()
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
        mRecordListFragment = RecordListFragment()
        supportFragmentManager.beginTransaction().add(R.id.frameLayout, mRecordListFragment).commit()
    }

    override fun onBackupButtonPressed(item: RecordData) {
        Toast.makeText(applicationContext, "已备份文件${item.guid}", Toast.LENGTH_SHORT).show()
    }

    override fun onRecordSelected(item: RecordData) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onLoadHeroImage(heroID: Int): String? {
        return HeroImage.getImageUrlByHeroID(heroID, assets)
    }
}

package xyz.piscesxp.pajtools.utility

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

class PermissionChecker {
    companion object {
        /**
         * @return true if GRANTED.
         * */
        fun checkPermission(context: Context, permission: String): Boolean {
            return ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        }

        fun requestPermission(activity: Activity, permission: String) {
            ActivityCompat.requestPermissions(activity, Array(1) { permission }, 0)
        }
    }
}
package com.desperado.audioandvideo

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import com.desperado.audioandvideo.audiorecord.AudioRecordActivity
import com.desperado.audioandvideo.camera.CameraActivity
import com.desperado.audioandvideo.drawbitmap.DrawBitmapActivity
import com.desperado.audioandvideo.opengles.OpenGLESDrawActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    val TAG = javaClass.name
    /**
     * 需要申请的运行时权限
     */
    private val permissions =
        arrayOf<String>(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA)
    /**
     * 被用户拒绝的权限列表
     */
    private val mPermissionList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkPermissions()
        tv_draw_bitmap.setOnClickListener {
            startActivity(Intent(this, DrawBitmapActivity::class.java))
        }
        tv_audio_record.setOnClickListener {
            startActivity(Intent(this, AudioRecordActivity::class.java))
        }
        tv_camera.setOnClickListener {
            startActivity(Intent(this, CameraActivity::class.java))
        }
        tv_opengles_draw.setOnClickListener {
            startActivity(Intent(this, OpenGLESDrawActivity::class.java))
        }
    }

    private fun checkPermissions() {
        // Marshmallow开始才用申请运行时权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (i in 0 until permissions.size) {
                if (ContextCompat.checkSelfPermission(this, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                    mPermissionList.add(permissions[i])
                }
            }
            if (!mPermissionList.isEmpty()) {
                val permissions = mPermissionList.toArray(arrayOfNulls<String>(mPermissionList.size))
                ActivityCompat.requestPermissions(this, permissions, 1001)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == 1001) {
            for (i in grantResults.indices) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    Log.i(TAG, permissions[i] + " 权限被用户禁止！")
                }
            }
        }
    }
}

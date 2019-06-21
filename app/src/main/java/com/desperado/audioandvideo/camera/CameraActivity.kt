package com.desperado.audioandvideo.camera

import android.graphics.SurfaceTexture
import android.hardware.Camera
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.SurfaceHolder
import android.view.TextureView
import com.desperado.audioandvideo.R
import kotlinx.android.synthetic.main.activity_camera.*
import java.util.*

/**
 *Created liuxun on 2018/12/19
 *Email:liuxun@yy.com
 */

class CameraActivity : AppCompatActivity() {
    var camera: Camera? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        camera = Camera.open()
        camera?.setDisplayOrientation(90)
        if (Random().nextBoolean()) {
            sv_camera.holder.addCallback(object : SurfaceHolder.Callback {
                override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
                }

                override fun surfaceDestroyed(holder: SurfaceHolder?) {
                    camera?.release()
                }

                override fun surfaceCreated(holder: SurfaceHolder?) {
                    camera?.setPreviewDisplay(holder)
                    camera?.startPreview()
                }

            })
        } else {
            ttv_camera.setSurfaceTextureListener(object : TextureView.SurfaceTextureListener {
                override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture?, width: Int, height: Int) {
                }

                override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) {
                }

                override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?): Boolean {
                    camera?.release()
                    return false
                }

                override fun onSurfaceTextureAvailable(surface: SurfaceTexture?, width: Int, height: Int) {
                    camera?.setPreviewTexture(surface)
                    camera?.startPreview()
                }

            })
        }
        camera?.setPreviewCallback { data, camera -> {

        } }
    }
}
package com.desperado.audioandvideo.drawbitmap

import android.graphics.BitmapFactory
import android.graphics.Paint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.SurfaceHolder
import com.desperado.audioandvideo.R
import kotlinx.android.synthetic.main.activity_draw_bitmap.*

/**
 *Created liuxun on 2018/12/19
 *Email:liuxun@yy.com
 */

class DrawBitmapActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_draw_bitmap)
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.jose)
        bitmap.density= resources.displayMetrics.densityDpi
        iv_draw.setImageBitmap(bitmap)
        sv_draw.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
            }

            override fun surfaceDestroyed(holder: SurfaceHolder?) {
            }

            override fun surfaceCreated(holder: SurfaceHolder?) {
                holder.let {
                    val paint = Paint()
                    paint.isAntiAlias = true
                    paint.style = Paint.Style.FILL
                    var canvas = it?.lockCanvas()
                    canvas?.drawBitmap(bitmap, 0f, 0f, paint)
                    it?.unlockCanvasAndPost(canvas)
                }
            }

        })
    }
}
package com.desperado.audioandvideo.drawbitmap

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.desperado.audioandvideo.R

/**
 *Created liuxun on 2018/12/19
 *Email:liuxun@yy.com
 */
class DrawBitmapView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    val paint : Paint?
    val bitmap : Bitmap?
    init {
        paint = Paint()
        paint.isAntiAlias = true
        paint.style = Paint.Style.STROKE
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.jose)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if(bitmap != null){
            canvas?.drawBitmap(bitmap, 10f ,10f, paint)
        }
    }
}
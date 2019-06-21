package com.desperado.audioandvideo.opengles

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10


/**
 *Created liuxun on 2019/2/21
 *Email:liuxun@yy.com
 */

class OpenGLESDrawActivity : AppCompatActivity() {
    var glSurfaceView: GLSurfaceView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        glSurfaceView = MyGLSerfaceView(this)
        setContentView(glSurfaceView)
    }

    class MyGLSerfaceView(context: Context?) : GLSurfaceView(context) {
        var myGLRenderer: MyGLRenderer

        init {
            setEGLContextClientVersion(2)
            myGLRenderer = MyGLRenderer()
            setRenderer(myGLRenderer)
        }
    }

    class MyGLRenderer : GLSurfaceView.Renderer {
        override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
            GLES20.glClearColor(66 / 255f, 244 / 255f, 125 / 255f, 1.0f)
        }

        override fun onDrawFrame(gl: GL10?) {
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        }

        override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
            GLES20.glViewport(0, 0, width, height)
        }
    }

    class Triangle {
        var floatBuffer: FloatBuffer
        val COORDS_PER_VERTEX = 3
        var triangleCoords = floatArrayOf(// in counterclockwise order:
                0.0f, 0.622008459f, 0.0f, // top
                -0.5f, -0.311004243f, 0.0f, // bottom left
                0.5f, -0.311004243f, 0.0f  // bottom right
        )

        init {
            // initialize vertex byte buffer for shape coordinates
            val bb = ByteBuffer.allocateDirect(
                    // (number of coordinate values * 4 bytes per float)
                    triangleCoords.size * 4)
            // use the device hardware's native byte order
            bb.order(ByteOrder.nativeOrder())

            // create a floating point buffer from the ByteBuffer
            floatBuffer = bb.asFloatBuffer()
            // add the coordinates to the FloatBuffer
            floatBuffer.put(triangleCoords)
            // set the buffer to read the first coordinate
            floatBuffer.position(0)
        }
    }
}
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
        var triangle: Triangle? = null
        override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
            GLES20.glClearColor(66 / 255f, 244 / 255f, 125 / 255f, 1.0f)
            triangle = Triangle()
        }

        override fun onDrawFrame(gl: GL10?) {
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
            triangle?.draw()
        }

        override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
            GLES20.glViewport(0, 0, width, height)
        }
    }

    //定义形状
    class Triangle {
        //OpenGLES对象，包含了你想要用来绘制一个或多个形状的shader。
        var program = 0
        var floatBuffer: FloatBuffer
        val COORDS_PER_VERTEX = 3 // 每个顶点的坐标数

        var triangleCoords = floatArrayOf(// in counterclockwise order:
            0.0f, 0.622008459f, 0.0f, // top
            -0.5f, -0.6311004243f, 0.0f, // bottom left
            0.15f, -0.311004243f, 0.0f  // bottom right
        )

        // 定义三角形的颜色——白色
        var color = floatArrayOf(1.0f, 1.0f, 1.0f, 1.0f)

        private val vertexCount = triangleCoords.size / COORDS_PER_VERTEX // 顶点个数
        private val vertexStride = COORDS_PER_VERTEX * 4 // 每个顶点四个字节

        //用于渲染形状的顶点的OpenGLES 图形代码
        private val vertexShaderCode = "attribute vec4 vPosition;" +
            "void main() {" +
            "  gl_Position = vPosition;" +
            "}"
        //用于渲染形状的外观（颜色或纹理）的OpenGLES 代码。
        private val fragmentShaderCode = (
            "precision mediump float;" +
                "uniform vec4 vColor;" +
                "void main() {" +
                "  gl_FragColor = vColor;" +
                "}")

        init {
            GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f); // 申请底色空间

            //为了绘制你的形状，你必须编译shader代码，添加它们到一个OpenGLES program 对象然后链接这个program。
            // 在renderer对象的构造器中做这些事情，从而只需做一次即可。
            var vertextShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
            var fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)
            program = GLES20.glCreateProgram()
            GLES20.glAttachShader(program, vertextShader)
            GLES20.glAttachShader(program, fragmentShader)
            GLES20.glLinkProgram(program)

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

        fun draw() {
            //将程序加入到OpenGLES2.0环境
            GLES20.glUseProgram(program)
            //获取顶点着色器的vPosition成员句柄
            var positionHandle = GLES20.glGetAttribLocation(program, "vPosition")
            //启用三角形顶点的句柄
            GLES20.glEnableVertexAttribArray(positionHandle)
            //准备三角形的坐标数据
            GLES20.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, floatBuffer);
            //获取片元着色器的vColor成员的句柄
            var colorHandle = GLES20.glGetUniformLocation(program, "vColor");
            //设置绘制三角形的颜色
            GLES20.glUniform4fv(colorHandle, 1, color, 0);
            //绘制三角形
            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);
            //禁止顶点数组的句柄
            GLES20.glDisableVertexAttribArray(positionHandle);
        }
    }
}
/*
* Shader们包含了OpenGLShading Language (GLSL)代码，必须在使用前编译
* */
fun loadShader(type: Int, shaderCode: String): Int {

    // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
    // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
    val shader = GLES20.glCreateShader(type)

    // add the source code to the shader and compile it
    GLES20.glShaderSource(shader, shaderCode)
    GLES20.glCompileShader(shader)

    return shader
}
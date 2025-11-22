package com.ocp.sdk.plugins

import android.opengl.GLES20
import com.ocp.sdk.gl.Texture2dProgram
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class BrightnessContrastPlugin : ShaderPlugin(
    name = "BrightnessContrast",
    version = 1,
    description = "Adjusts brightness and contrast of the image.",
    fragmentShaderCode = FRAGMENT_SHADER
) {

    private var brightnessLoc = -1
    private var contrastLoc = -1
    
    // Default values
    private var brightness = 0.0f
    private var contrast = 1.0f

    // We need to override process because we need to set uniforms.
    // However, the base class currently doesn't expose the program handle easily 
    // or the draw call. 
    // For this prototype, I will implement the full GL logic here, 
    // assuming the base class was just a marker or I'll duplicate the GL code for now 
    // to ensure it works without refactoring Texture2dProgram too much.
    
    private var programHandle = 0
    private var uMVPMatrixLoc = -1
    private var uTexMatrixLoc = -1
    private var aPositionLoc = -1
    private var aTextureCoordLoc = -1
    
    private val mIdentityMatrix = FloatArray(16)
    
    // Geometry (Same as Engine for now)
    private val FULL_RECTANGLE_COORDS = floatArrayOf(
        -1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f, 1.0f
    )
    private val FULL_RECTANGLE_TEX_COORDS = floatArrayOf(
        0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f
    )
    private val fullRectangleBuf: FloatBuffer
    private val fullRectangleTexBuf: FloatBuffer

    init {
        android.opengl.Matrix.setIdentityM(mIdentityMatrix, 0)
        fullRectangleBuf = createFloatBuffer(FULL_RECTANGLE_COORDS)
        fullRectangleTexBuf = createFloatBuffer(FULL_RECTANGLE_TEX_COORDS)
    }

    override fun init(params: Map<String, Any>) {
        super.init(params)
        if (params.containsKey("brightness")) {
            val p = params["brightness"]
            brightness = (p as? Number)?.toFloat() ?: (p as? String)?.toFloatOrNull() ?: 0.0f
        }
        if (params.containsKey("contrast")) {
            val p = params["contrast"]
            contrast = (p as? Number)?.toFloat() ?: (p as? String)?.toFloatOrNull() ?: 1.0f
        }

        programHandle = createProgram(VERTEX_SHADER, FRAGMENT_SHADER)
        aPositionLoc = GLES20.glGetAttribLocation(programHandle, "aPosition")
        aTextureCoordLoc = GLES20.glGetAttribLocation(programHandle, "aTextureCoord")
        uMVPMatrixLoc = GLES20.glGetUniformLocation(programHandle, "uMVPMatrix")
        uTexMatrixLoc = GLES20.glGetUniformLocation(programHandle, "uTexMatrix")
        brightnessLoc = GLES20.glGetUniformLocation(programHandle, "uBrightness")
        contrastLoc = GLES20.glGetUniformLocation(programHandle, "uContrast")
    }

    override fun process(inputTextureId: Int, outputTextureId: Int, timestampNs: Long) {
        // Note: This plugin assumes it draws to the currently bound framebuffer (outputTextureId).
        // In a real graph, we'd bind the FBO for outputTextureId here.
        // For this simple test, we assume the engine set up the target.
        
        GLES20.glUseProgram(programHandle)

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, inputTextureId)

        GLES20.glUniformMatrix4fv(uMVPMatrixLoc, 1, false, mIdentityMatrix, 0)
        GLES20.glUniformMatrix4fv(uTexMatrixLoc, 1, false, mIdentityMatrix, 0)
        
        GLES20.glUniform1f(brightnessLoc, brightness)
        GLES20.glUniform1f(contrastLoc, contrast)

        GLES20.glEnableVertexAttribArray(aPositionLoc)
        GLES20.glVertexAttribPointer(aPositionLoc, 2, GLES20.GL_FLOAT, false, 8, fullRectangleBuf)

        GLES20.glEnableVertexAttribArray(aTextureCoordLoc)
        GLES20.glVertexAttribPointer(aTextureCoordLoc, 2, GLES20.GL_FLOAT, false, 8, fullRectangleTexBuf)

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)

        GLES20.glDisableVertexAttribArray(aPositionLoc)
        GLES20.glDisableVertexAttribArray(aTextureCoordLoc)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
        GLES20.glUseProgram(0)
    }

    override fun destroy() {
        GLES20.glDeleteProgram(programHandle)
    }

    private fun createFloatBuffer(coords: FloatArray): FloatBuffer {
        val bb = ByteBuffer.allocateDirect(coords.size * 4)
        bb.order(ByteOrder.nativeOrder())
        val fb = bb.asFloatBuffer()
        fb.put(coords)
        fb.position(0)
        return fb
    }
    
    // Helper to compile shader (Duplicated from Texture2dProgram for now)
    private fun createProgram(vertexSource: String, fragmentSource: String): Int {
        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexSource)
        val pixelShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource)
        val program = GLES20.glCreateProgram()
        GLES20.glAttachShader(program, vertexShader)
        GLES20.glAttachShader(program, pixelShader)
        GLES20.glLinkProgram(program)
        return program
    }

    private fun loadShader(type: Int, source: String): Int {
        val shader = GLES20.glCreateShader(type)
        GLES20.glShaderSource(shader, source)
        GLES20.glCompileShader(shader)
        return shader
    }

    companion object {
        private const val VERTEX_SHADER =
            "uniform mat4 uMVPMatrix;\n" +
            "uniform mat4 uTexMatrix;\n" +
            "attribute vec4 aPosition;\n" +
            "attribute vec4 aTextureCoord;\n" +
            "varying vec2 vTextureCoord;\n" +
            "void main() {\n" +
            "    gl_Position = uMVPMatrix * aPosition;\n" +
            "    vTextureCoord = (uTexMatrix * aTextureCoord).xy;\n" +
            "}\n"

        private const val FRAGMENT_SHADER =
            "precision mediump float;\n" +
            "varying vec2 vTextureCoord;\n" +
            "uniform sampler2D sTexture;\n" +
            "uniform float uBrightness;\n" +
            "uniform float uContrast;\n" +
            "void main() {\n" +
            "    vec4 color = texture2D(sTexture, vTextureCoord);\n" +
            "    color.rgb += uBrightness;\n" +
            "    color.rgb = (color.rgb - 0.5) * uContrast + 0.5;\n" +
            "    gl_FragColor = color;\n" +
            "}\n"
    }
}

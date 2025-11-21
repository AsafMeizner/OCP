package com.ocp.sdk.plugins

import android.graphics.Bitmap
import android.opengl.GLES20
import android.opengl.GLUtils
import com.ocp.sdk.gl.Texture2dProgram
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class LutPlugin : ShaderPlugin(
    name = "LutPlugin",
    version = 1,
    description = "Applies a 3D LUT for color grading.",
    fragmentShaderCode = FRAGMENT_SHADER
) {

    private var lutTextureId = -1
    private var lutLoc = -1
    private var intensityLoc = -1
    
    private var intensity = 1.0f
    private var lutBitmap: Bitmap? = null

    private var programHandle = 0
    private var uMVPMatrixLoc = -1
    private var uTexMatrixLoc = -1
    private var aPositionLoc = -1
    private var aTextureCoordLoc = -1
    
    private val mIdentityMatrix = FloatArray(16)
    
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
        if (params.containsKey("intensity")) {
            intensity = (params["intensity"] as Number).toFloat()
        }
        if (params.containsKey("lutBitmap")) {
            lutBitmap = params["lutBitmap"] as? Bitmap
        }

        programHandle = createProgram(VERTEX_SHADER, FRAGMENT_SHADER)
        aPositionLoc = GLES20.glGetAttribLocation(programHandle, "aPosition")
        aTextureCoordLoc = GLES20.glGetAttribLocation(programHandle, "aTextureCoord")
        uMVPMatrixLoc = GLES20.glGetUniformLocation(programHandle, "uMVPMatrix")
        uTexMatrixLoc = GLES20.glGetUniformLocation(programHandle, "uTexMatrix")
        lutLoc = GLES20.glGetUniformLocation(programHandle, "sLut")
        intensityLoc = GLES20.glGetUniformLocation(programHandle, "uIntensity")

        if (lutBitmap != null) {
            loadLutTexture(lutBitmap!!)
        }
    }

    private fun loadLutTexture(bitmap: Bitmap) {
        val textures = IntArray(1)
        GLES20.glGenTextures(1, textures, 0)
        lutTextureId = textures[0]
        
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, lutTextureId)
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR.toFloat())
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR.toFloat())
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE)
        
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
    }

    override fun process(inputTextureId: Int, outputTextureId: Int, timestampNs: Long) {
        GLES20.glUseProgram(programHandle)

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, inputTextureId)
        GLES20.glUniform1i(GLES20.glGetUniformLocation(programHandle, "sTexture"), 0)

        if (lutTextureId != -1) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE1)
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, lutTextureId)
            GLES20.glUniform1i(lutLoc, 1)
        }

        GLES20.glUniformMatrix4fv(uMVPMatrixLoc, 1, false, mIdentityMatrix, 0)
        GLES20.glUniformMatrix4fv(uTexMatrixLoc, 1, false, mIdentityMatrix, 0)
        GLES20.glUniform1f(intensityLoc, intensity)

        GLES20.glEnableVertexAttribArray(aPositionLoc)
        GLES20.glVertexAttribPointer(aPositionLoc, 2, GLES20.GL_FLOAT, false, 8, fullRectangleBuf)

        GLES20.glEnableVertexAttribArray(aTextureCoordLoc)
        GLES20.glVertexAttribPointer(aTextureCoordLoc, 2, GLES20.GL_FLOAT, false, 8, fullRectangleTexBuf)

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)

        GLES20.glDisableVertexAttribArray(aPositionLoc)
        GLES20.glDisableVertexAttribArray(aTextureCoordLoc)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0) // Unbind 0
        GLES20.glActiveTexture(GLES20.GL_TEXTURE1)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0) // Unbind 1
        GLES20.glUseProgram(0)
    }

    override fun destroy() {
        GLES20.glDeleteProgram(programHandle)
        if (lutTextureId != -1) {
            GLES20.glDeleteTextures(1, intArrayOf(lutTextureId), 0)
        }
    }

    private fun createFloatBuffer(coords: FloatArray): FloatBuffer {
        val bb = ByteBuffer.allocateDirect(coords.size * 4)
        bb.order(ByteOrder.nativeOrder())
        val fb = bb.asFloatBuffer()
        fb.put(coords)
        fb.position(0)
        return fb
    }

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

        // Simple LUT shader (assuming 512x512 square LUT for 64x64x64)
        private const val FRAGMENT_SHADER =
            "precision mediump float;\n" +
            "varying vec2 vTextureCoord;\n" +
            "uniform sampler2D sTexture;\n" +
            "uniform sampler2D sLut;\n" +
            "uniform float uIntensity;\n" +
            "void main() {\n" +
            "    vec4 textureColor = texture2D(sTexture, vTextureCoord);\n" +
            "    float blueColor = textureColor.b * 63.0;\n" +
            "    vec2 quad1;\n" +
            "    quad1.y = floor(floor(blueColor) / 8.0);\n" +
            "    quad1.x = floor(blueColor) - (quad1.y * 8.0);\n" +
            "    vec2 quad2;\n" +
            "    quad2.y = floor(ceil(blueColor) / 8.0);\n" +
            "    quad2.x = ceil(blueColor) - (quad2.y * 8.0);\n" +
            "    vec2 texPos1;\n" +
            "    texPos1.x = (quad1.x * 0.125) + 0.5/512.0 + ((0.125 - 1.0/512.0) * textureColor.r);\n" +
            "    texPos1.y = (quad1.y * 0.125) + 0.5/512.0 + ((0.125 - 1.0/512.0) * textureColor.g);\n" +
            "    vec2 texPos2;\n" +
            "    texPos2.x = (quad2.x * 0.125) + 0.5/512.0 + ((0.125 - 1.0/512.0) * textureColor.r);\n" +
            "    texPos2.y = (quad2.y * 0.125) + 0.5/512.0 + ((0.125 - 1.0/512.0) * textureColor.g);\n" +
            "    vec4 newColor1 = texture2D(sLut, texPos1);\n" +
            "    vec4 newColor2 = texture2D(sLut, texPos2);\n" +
            "    vec4 newColor = mix(newColor1, newColor2, fract(blueColor));\n" +
            "    gl_FragColor = mix(textureColor, vec4(newColor.rgb, textureColor.a), uIntensity);\n" +
            "}\n"
    }
}

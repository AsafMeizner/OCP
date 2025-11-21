package com.ocp.sdk.plugins

import android.opengl.GLES20
import com.ocp.sdk.Plugin
import com.ocp.sdk.PluginCaps
import com.ocp.sdk.gl.FramebufferObject

class FaceMaskPlugin : ShaderPlugin() {

    private var faceX: Float = 0.5f
    private var faceY: Float = 0.5f
    private var faceSize: Float = 0.0f
    private var hasFace: Boolean = false

    private var uFacePosLoc = -1
    private var uFaceSizeLoc = -1
    private var uHasFaceLoc = -1

    override fun getCaps(): PluginCaps {
        return PluginCaps(
            name = "Face Mask",
            description = "Overlays a red circle on detected faces",
            author = "OCP Team",
            version = 1,
            parameters = listOf() // No user params, driven by ML
        )
    }

    override fun getFragmentShaderCode(): String {
        return """
            precision mediump float;
            varying vec2 vTextureCoord;
            uniform sampler2D sTexture;
            uniform vec2 uFacePos; // Normalized (0-1)
            uniform float uFaceSize; // Normalized radius
            uniform int uHasFace;

            void main() {
                vec4 color = texture2D(sTexture, vTextureCoord);
                
                if (uHasFace == 1) {
                    float dist = distance(vTextureCoord, uFacePos);
                    if (dist < uFaceSize) {
                        // Simple red overlay
                        color.r = 1.0;
                        color.g = color.g * 0.5;
                        color.b = color.b * 0.5;
                    }
                }
                
                gl_FragColor = color;
            }
        """
    }

    override fun onProgramCreated(programId: Int) {
        super.onProgramCreated(programId)
        uFacePosLoc = GLES20.glGetUniformLocation(programId, "uFacePos")
        uFaceSizeLoc = GLES20.glGetUniformLocation(programId, "uFaceSize")
        uHasFaceLoc = GLES20.glGetUniformLocation(programId, "uHasFace")
    }

    override fun onDraw(programId: Int) {
        super.onDraw(programId)
        GLES20.glUniform2f(uFacePosLoc, faceX, faceY)
        GLES20.glUniform1f(uFaceSizeLoc, faceSize)
        GLES20.glUniform1i(uHasFaceLoc, if (hasFace) 1 else 0)
    }

    // Called by the detector
    fun updateFace(x: Float, y: Float, size: Float) {
        faceX = x
        faceY = y
        faceSize = size
        hasFace = true
    }

    fun clearFace() {
        hasFace = false
    }
}

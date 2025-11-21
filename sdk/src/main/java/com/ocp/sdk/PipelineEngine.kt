package com.ocp.sdk

import android.graphics.SurfaceTexture
import android.opengl.GLES20
import android.opengl.Matrix
import android.view.Surface
import com.ocp.sdk.gl.EglCore
import com.ocp.sdk.gl.Texture2dProgram
import com.ocp.sdk.gl.WindowSurface
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.util.concurrent.atomic.AtomicBoolean

class PipelineEngine {

    private var eglThread: Thread? = null
    private val isRunning = AtomicBoolean(false)
    private var previewSurface: Surface? = null
    private val lock = Object()

    // GL State (Accessed only on GL Thread)
    private var eglCore: EglCore? = null
    private var windowSurface: WindowSurface? = null
    private var textureId: Int = -1
    private var surfaceTexture: SurfaceTexture? = null
    private var fullFrameBlit: Texture2dProgram? = null
    
    // Plugin State
    private val activePlugins = mutableListOf<com.ocp.sdk.Plugin>()
    private var fboA: com.ocp.sdk.gl.FramebufferObject? = null
    private var fboB: com.ocp.sdk.gl.FramebufferObject? = null
    
    // ... (Existing init) ...

    fun setPipeline(plugins: List<com.ocp.sdk.Plugin>) {
        synchronized(lock) {
            activePlugins.clear()
            activePlugins.addAll(plugins)
            lock.notifyAll()
        }
    }

    // ... (Existing methods) ...

    private fun loop() {
        eglCore = EglCore(null, 0)
        val offscreenSurface = com.ocp.sdk.gl.EglSurfaceBase(eglCore!!)
        offscreenSurface.createOffscreenSurface(1, 1)
        offscreenSurface.makeCurrent()

        // Initialize FBOs (Assuming 1080p for now, should be dynamic based on camera)
        fboA = com.ocp.sdk.gl.FramebufferObject(1920, 1080)
        fboB = com.ocp.sdk.gl.FramebufferObject(1920, 1080)

        fullFrameBlit = Texture2dProgram(Texture2dProgram.ProgramType.TEXTURE_EXT)
        textureId = fullFrameBlit!!.createTextureObject()
        surfaceTexture = SurfaceTexture(textureId)

        println("Engine: GL Init complete. TextureID=$textureId")

        while (isRunning.get()) {
            var surface: Surface? = null
            var recording = false
            var encoder: VideoEncoder? = null
            var plugins: List<com.ocp.sdk.Plugin> = emptyList()
            
            synchronized(lock) {
                surface = previewSurface
                recording = isRecording
                encoder = videoEncoder
                plugins = ArrayList(activePlugins)
            }

            // ... (Surface handling same as before) ...
            if (surface != null && windowSurface == null) {
                plugin.process(currentInputTexId, currentOutputFbo!!.textureId, surfaceTexture!!.timestamp)
                
                currentOutputFbo!!.unbind()
                
                // Swap
                currentInputTexId = currentOutputFbo!!.textureId
                currentOutputFbo = if (currentOutputFbo == fboA) fboB else fboA
            }
            
            // currentInputTexId now holds the final result
            
            // 3. Draw to Preview
            if (windowSurface != null) {
                windowSurface!!.makeCurrent()
                GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
                drawTexture2D(currentInputTexId)
                windowSurface!!.swapBuffers()
            }
            
            // 4. Draw to Encoder
            if (encoderSurface != null) {
                encoderSurface!!.makeCurrent()
                GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
                drawTexture2D(currentInputTexId)
                // encoderSurface!!.setPresentationTime(surfaceTexture!!.timestamp)
                encoderSurface!!.swapBuffers()
                videoEncoder?.drainEncoder(false)
            }

            try {
                Thread.sleep(16)
            } catch (e: InterruptedException) {
                break
            }
        }

        println("Engine: Releasing GL resources")
        fboA?.release()
        fboB?.release()
        windowSurface?.release()
        encoderSurface?.release()
        offscreenSurface.releaseEglSurface()
        fullFrameBlit?.release()
        eglCore?.release()
    }
    
    // Helper to draw a standard 2D texture (result of FBO) to screen
    private var tex2dProgram: Texture2dProgram? = null
    
    private fun drawTexture2D(texId: Int) {
        if (tex2dProgram == null) {
            tex2dProgram = Texture2dProgram(Texture2dProgram.ProgramType.TEXTURE_2D)
        }
        tex2dProgram?.draw(
            mIdentityMatrix,
            mIdentityMatrix, // Identity for 2D texture (already transformed)
            texId,
            fullRectangleBuf,
            0,
            4,
            2,
            8,
            fullRectangleTexBuf,
            8
        )
    }

    private fun createFloatBuffer(coords: FloatArray): FloatBuffer {
        val bb = ByteBuffer.allocateDirect(coords.size * 4)
        bb.order(ByteOrder.nativeOrder())
        val fb = bb.asFloatBuffer()
        fb.put(coords)
        fb.position(0)
        return fb
    }
}

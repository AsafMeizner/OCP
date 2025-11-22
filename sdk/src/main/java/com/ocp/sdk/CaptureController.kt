package com.ocp.sdk

import android.content.Context
import android.view.SurfaceHolder
import com.ocp.sdk.audio.AudioEngine
import com.ocp.sdk.audio.AudioPlugin

class CaptureController(context: Context) {

    private val cameraInput = CameraInput(context)
    private val pipelineEngine = PipelineEngine()
    private val audioEngine = AudioEngine()
    private val pluginLoader = PluginLoader()
    
    // Keep track of encoder to wire audio
    private var currentEncoder: VideoEncoder? = null

    init {
        // Start audio engine immediately for monitoring/processing
        audioEngine.start()
    }

    fun start(cameraId: String, pipelineId: String) {
        cameraInput.start(cameraId, pipelineEngine.surfaceTexture)
    }

    fun setPreviewSurface(holder: SurfaceHolder) {
        holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                pipelineEngine.setPreviewSurface(holder.surface)
            }

            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
                // Handle resize
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                pipelineEngine.setPreviewSurface(null)
            }
        })
    }

    fun startRecording(outputPath: String) {
        val encoder = VideoEncoder(1920, 1080, 10000000, outputPath)
        currentEncoder = encoder
        
        // Wire Audio
        audioEngine.setAudioSink(object : AudioEngine.AudioSink {
            override fun onAudioData(data: ByteArray, size: Int, timestampNs: Long) {
                currentEncoder?.queueAudio(data, size, timestampNs)
            }
        })
        
        pipelineEngine.startRecording(encoder)
        println("Recording started to: $outputPath")
    }
    
    fun stopRecording() {
        audioEngine.setAudioSink(null)
        pipelineEngine.stopRecording()
        currentEncoder = null
    }

    fun setPipeline(pipeline: com.ocp.shared.PipelineDefinition) {
        val plugins = pluginLoader.instantiatePipeline(pipeline)
        
        // Separate Video and Audio plugins
        val audioPlugins = plugins.filterIsInstance<AudioPlugin>()
        val videoPlugins = plugins.filter { it !is AudioPlugin }
        
        pipelineEngine.setPlugins(videoPlugins)
        audioEngine.setPlugins(audioPlugins)
    }

    fun stop() {
        stopRecording()
        cameraInput.stop()
        audioEngine.stop()
        pipelineEngine.stop()
    }
}

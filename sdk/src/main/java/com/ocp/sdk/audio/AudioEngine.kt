package com.ocp.sdk.audio

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import java.util.concurrent.atomic.AtomicBoolean

class AudioEngine {
    private val sampleRate = 44100
    private val channelConfig = AudioFormat.CHANNEL_IN_MONO
    private val audioFormat = AudioFormat.ENCODING_PCM_16BIT
    private val bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)

    private var audioRecord: AudioRecord? = null
    private var isRunning = AtomicBoolean(false)
    private var recordingThread: Thread? = null
    
    private val plugins = mutableListOf<AudioPlugin>()

    @SuppressLint("MissingPermission")
    fun start() {
        if (isRunning.get()) return

        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            sampleRate,
            channelConfig,
            audioFormat,
            bufferSize
        )

        if (audioRecord?.state != AudioRecord.STATE_INITIALIZED) {
            println("AudioEngine: Failed to initialize AudioRecord")
            return
        }

        isRunning.set(true)
        audioRecord?.startRecording()

        recordingThread = Thread {
            val buffer = ShortArray(bufferSize)
            while (isRunning.get()) {
                val readResult = audioRecord?.read(buffer, 0, bufferSize) ?: 0
                if (readResult > 0) {
                    var processedData = buffer
                    
                    // Apply plugins
                    synchronized(plugins) {
                        for (plugin in plugins) {
                            processedData = plugin.process(processedData, readResult)
                        }
                    }

                    // TODO: Pass processedData to Encoder or AudioTrack
                }
            }
        }
        recordingThread?.start()
    }

    fun stop() {
        isRunning.set(false)
        try {
            recordingThread?.join()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        
        audioRecord?.stop()
        audioRecord?.release()
        audioRecord = null
    }

    fun addPlugin(plugin: AudioPlugin) {
        synchronized(plugins) {
            plugins.add(plugin)
        }
    }
    
    fun clearPlugins() {
        synchronized(plugins) {
            plugins.clear()
        }
    }
}

package com.ocp.sdk.audio.plugins

import com.ocp.sdk.audio.AudioPlugin

class ReverbPlugin : AudioPlugin {
    private val delayBuffer = ShortArray(44100) // 1 second buffer at 44.1kHz
    private var bufferIndex = 0
    private var decay = 0.5f

    override fun init(params: Map<String, Any>) {
        super.init(params)
        if (params.containsKey("decay")) {
            val p = params["decay"]
            decay = (p as? Number)?.toFloat() ?: (p as? String)?.toFloatOrNull() ?: 0.5f
        }
    }

    override fun getName(): String = "Simple Reverb"

    override fun process(audioData: ShortArray, size: Int): ShortArray {
        val output = ShortArray(size)
        for (i in 0 until size) {
            val inputSample = audioData[i]
            val delaySample = delayBuffer[bufferIndex]
            
            // Simple feedback reverb
            val processed = (inputSample + delaySample * decay).toInt().toShort()
            output[i] = processed
            
            // Update buffer
            delayBuffer[bufferIndex] = processed
            bufferIndex = (bufferIndex + 1) % delayBuffer.size
        }
        return output
    }
}

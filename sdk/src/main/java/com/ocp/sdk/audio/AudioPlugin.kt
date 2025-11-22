package com.ocp.sdk.audio

import com.ocp.sdk.Plugin
import com.ocp.sdk.PluginCaps

interface AudioPlugin : Plugin {
    fun getName(): String
    fun process(audioData: ShortArray, size: Int): ShortArray

    // Default implementations for Video Plugin methods
    override val caps: PluginCaps
        get() = PluginCaps(
            name = getName(),
            version = 1,
            description = "Audio Plugin",
            author = "Unknown",
            tags = emptyList(),
            inputFormats = emptyList(),
            outputFormats = emptyList(),
            requiresGpu = false,
            requiresAi = false
        )

    override fun init(params: Map<String, Any>) {
        // Default no-op
    }

    override fun process(inputTextureId: Int, outputTextureId: Int, timestampNs: Long) {
        // Should not be called by PipelineEngine if filtered correctly
    }

    override fun destroy() {
        // Default no-op
    }
}

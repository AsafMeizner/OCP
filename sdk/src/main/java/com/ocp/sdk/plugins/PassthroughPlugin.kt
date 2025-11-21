package com.ocp.sdk.plugins

import com.ocp.sdk.PixelFormat
import com.ocp.sdk.Plugin
import com.ocp.sdk.PluginCaps

class PassthroughPlugin : Plugin {
    override val caps: PluginCaps = PluginCaps(
        name = "Passthrough",
        version = 1,
        description = "A no-op plugin that passes the input texture to the output.",
        author = "OCP Team",
        tags = listOf("utility", "debug"),
        inputFormats = listOf(PixelFormat.OES_TEXTURE, PixelFormat.RGBA_8888),
        outputFormats = listOf(PixelFormat.OES_TEXTURE, PixelFormat.RGBA_8888)
    )

    override fun init(params: Map<String, Any>) {
        println("PassthroughPlugin initialized with params: $params")
    }

    override fun process(inputTextureId: Int, outputTextureId: Int, timestampNs: Long) {
        // In a real implementation, this would perform a GL copy or draw a quad.
        // For this prototype, we just log.
        // println("PassthroughPlugin processing frame at $timestampNs")
    }

    override fun destroy() {
        println("PassthroughPlugin destroyed")
    }
}

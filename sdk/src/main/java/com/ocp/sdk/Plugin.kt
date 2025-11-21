package com.ocp.sdk

/**
 * Represents the capabilities of a plugin.
 */
data class PluginCaps(
    val name: String,
    val version: Int,
    val description: String,
    val author: String,
    val tags: List<String>,
    val inputFormats: List<PixelFormat>,
    val outputFormats: List<PixelFormat>,
    val requiresGpu: Boolean = true,
    val requiresAi: Boolean = false
)

enum class PixelFormat {
    RGBA_8888,
    YUV_420,
    OES_TEXTURE
}

/**
 * Base interface for all OCP plugins.
 */
interface Plugin {
    val caps: PluginCaps

    /**
     * Called when the plugin is initialized.
     * @param params Configuration parameters for the plugin.
     */
    fun init(params: Map<String, Any>)

    /**
     * Process a frame.
     * @param inputTextureId OpenGL texture ID of the input frame.
     * @param outputTextureId OpenGL texture ID where the result should be drawn.
     * @param timestampNs Timestamp of the frame in nanoseconds.
     */
    fun process(inputTextureId: Int, outputTextureId: Int, timestampNs: Long)

    /**
     * Called when the plugin is destroyed.
     */
    fun destroy()
}

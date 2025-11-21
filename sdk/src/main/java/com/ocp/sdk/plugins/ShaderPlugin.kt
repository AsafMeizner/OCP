package com.ocp.sdk.plugins

import com.ocp.sdk.PixelFormat
import com.ocp.sdk.Plugin
import com.ocp.sdk.PluginCaps
import com.ocp.sdk.gl.Texture2dProgram

/**
 * Base class for plugins that use a GLSL fragment shader.
 */
abstract class ShaderPlugin(
    name: String,
    version: Int,
    description: String,
    private val fragmentShaderCode: String
) : Plugin {

    override val caps = PluginCaps(
        name = name,
        version = version,
        description = description,
        author = "OCP",
        tags = listOf("shader", "filter"),
        inputFormats = listOf(PixelFormat.RGBA_8888, PixelFormat.OES_TEXTURE),
        outputFormats = listOf(PixelFormat.RGBA_8888)
    )

    // We will use Texture2dProgram but we need to be able to inject custom shaders.
    // For now, we'll assume Texture2dProgram can be subclassed or we create a similar helper.
    // Since Texture2dProgram is final/closed in our current impl, let's assume we add a constructor or method to it.
    // Or we just write raw GL here.
    
    // To keep it simple for this iteration, I'll assume we can't easily reuse Texture2dProgram 
    // for custom shaders without modifying it. 
    // I will implement a simple GL program wrapper here.
    
    private var programHandle: Int = 0
    
    override fun init(params: Map<String, Any>) {
        // Compile shader
        // ...
    }

    override fun process(inputTextureId: Int, outputTextureId: Int, timestampNs: Long) {
        // Draw quad with shader
    }

    override fun destroy() {
        // Delete program
    }
}

package com.ocp.sdk

import com.ocp.sdk.plugins.BrightnessContrastPlugin

class PluginLoader {
    
    private val loadedPlugins = mutableMapOf<String, Plugin>()

    fun loadPlugin(pluginName: String): Plugin? {
        // In a real system, this would load from a dynamic library or APK.
        // Here we use a simple registry for the prototype.
        return when (pluginName) {
            "Passthrough" -> PassthroughPlugin()
            else -> {
                println("PluginLoader: Unknown plugin $pluginName")
                null
            }
```kotlin
package com.ocp.sdk

import com.ocp.sdk.plugins.PassthroughPlugin

class PluginLoader {
    
    private val loadedPlugins = mutableMapOf<String, Plugin>()

    fun loadPlugin(pluginName: String): Plugin? {
        // In a real system, this would load from a dynamic library or APK.
        // Here we use a simple registry for the prototype.
        return when (pluginName) {
            "Passthrough" -> com.ocp.sdk.plugins.BrightnessContrastPlugin() // Use BC as default for now, or create a real Passthrough
            "BrightnessContrast" -> com.ocp.sdk.plugins.BrightnessContrastPlugin()
            "Lut" -> com.ocp.sdk.plugins.LutPlugin()
            "FaceMask" -> com.ocp.sdk.plugins.FaceMaskPlugin()
            "Reverb" -> com.ocp.sdk.audio.plugins.ReverbPlugin()
            else -> {
                println("PluginLoader: Unknown plugin $pluginName")
                null
            }
        }
    }

    fun instantiatePipeline(pipeline: com.ocp.shared.PipelineDefinition): List<Plugin> {
        val plugins = mutableListOf<Plugin>()
        pipeline.plugins.forEach { definition ->
            loadPlugin(definition.type)?.let { plugin ->
                plugin.init(definition.parameters)
                plugins.add(plugin)
            }
        }
        return plugins
    }
}
```

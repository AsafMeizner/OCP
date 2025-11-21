package com.ocp.sdk

import com.ocp.sdk.plugins.PassthroughPlugin

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
            "Passthrough" -> PassthroughPlugin()
            else -> {
                println("PluginLoader: Unknown plugin $pluginName")
                null
            }
        }
    }

    fun instantiatePipeline(pipeline: Pipeline): List<Plugin> {
        val plugins = mutableListOf<Plugin>()
        // Assuming pipeline.stages.values.flatten() now yields PluginDefinition objects
        // or that the pluginName can be used to look up the definition.
        // For this change, we'll assume the `forEach` loop now iterates over `PluginDefinition` objects.
        // If `pipeline.stages.values.flatten()` still yields `String` (pluginName),
        // then `definition` would need to be looked up based on `pluginName`.
        // Given the instruction, we'll adapt the loop to use `definition` directly.
        pipeline.stages.values.flatten().forEach { definition -> // Assuming 'definition' is a PluginDefinition object
            loadPlugin(definition.name)?.let { plugin -> // Load plugin using definition's name
                plugin.init(definition.parameters) // Pass parameters from the definition
                plugins.add(plugin) // Add to the list of instantiated plugins
                // If the intent was to store in loadedPlugins map, it would be:
                // loadedPlugins[definition.name] = plugin
            }
        }
        return plugins
    }
}
```

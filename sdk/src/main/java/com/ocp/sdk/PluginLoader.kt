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
        pipeline.stages.values.flatten().forEach { pluginName ->
            loadPlugin(pluginName)?.let { 
                it.init(emptyMap()) // TODO: Pass params from pipeline config
                plugins.add(it)
            }
        }
        return plugins
    }
}

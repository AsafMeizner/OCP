package com.ocp.sdk

import com.ocp.sdk.plugins.BrightnessContrastPlugin
import com.ocp.shared.PipelineDefinition
import com.ocp.shared.PluginDefinition
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PluginLoaderTest {

    @Test
    fun testLoadPlugin() {
        val loader = PluginLoader()
        val plugin = loader.loadPlugin("BrightnessContrast")
        assertTrue(plugin is BrightnessContrastPlugin)
    }

    @Test
    fun testInstantiatePipeline() {
        val loader = PluginLoader()
        val pipelineDef = PipelineDefinition(
            id = "test",
            name = "Test Pipeline",
            plugins = listOf(
                PluginDefinition("1", "BrightnessContrast", emptyMap())
            )
        )
        
        val plugins = loader.instantiatePipeline(pipelineDef)
        assertEquals(1, plugins.size)
        assertTrue(plugins[0] is BrightnessContrastPlugin)
    }
}

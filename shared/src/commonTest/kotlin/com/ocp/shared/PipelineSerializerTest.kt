package com.ocp.shared

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PipelineSerializerTest {

    @Test
    fun testSerialization() {
        val plugin = PluginDefinition(
            id = "p1",
            type = "BrightnessContrast",
            parameters = mapOf("brightness" to "0.5")
        )
        val pipeline = PipelineDefinition(
            id = "pipe1",
            name = "Test Pipeline",
            plugins = listOf(plugin)
        )

        val json = PipelineSerializer.toJson(pipeline)
        
        assertTrue(json.contains("Test Pipeline"))
        assertTrue(json.contains("BrightnessContrast"))
        
        val decoded = PipelineSerializer.fromJson(json)
        assertEquals(pipeline, decoded)
    }
}

package com.ocp.shared

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class PipelineDefinition(
    val id: String,
    val name: String,
    val plugins: List<PluginDefinition>
)

@Serializable
data class PluginDefinition(
    val id: String,
    val type: String,
    val parameters: Map<String, String>
)

object PipelineSerializer {
    private val json = Json { ignoreUnknownKeys = true }

    fun toJson(pipeline: PipelineDefinition): String {
        return json.encodeToString(PipelineDefinition.serializer(), pipeline)
    }

    fun fromJson(jsonString: String): PipelineDefinition {
        return json.decodeFromString(PipelineDefinition.serializer(), jsonString)
    }
}

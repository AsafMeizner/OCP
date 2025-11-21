package com.ocp.app.data

import com.ocp.app.network.NetworkClient
import com.ocp.app.network.PluginDto
import com.ocp.shared.PipelineDefinition
import com.ocp.shared.PipelineSerializer

object PipelineRepository {
    suspend fun uploadPipeline(pipeline: PipelineDefinition) {
        // TODO: Add API endpoint for pipeline upload
        // NetworkClient.api.uploadPipeline(pipeline)
        println("Uploading pipeline: ${pipeline.name}")
    }

    suspend fun downloadPipeline(id: String): PipelineDefinition? {
        // TODO: Add API endpoint for pipeline download
        // return NetworkClient.api.getPipeline(id)
        return null
    }
    
    suspend fun getMyPipelines(): List<PipelineDefinition> {
        // Mock data
        return listOf(
            PipelineDefinition("1", "My Cool Filter", emptyList()),
            PipelineDefinition("2", "Portrait Mode", emptyList())
        )
    }
}

package com.ocp.app.data

import com.ocp.app.network.NetworkClient
import com.ocp.app.network.PluginDto
import com.ocp.shared.PipelineDefinition
import com.ocp.shared.PipelineSerializer

object PipelineRepository {
    suspend fun uploadPipeline(pipeline: PipelineDefinition) {
        try {
            NetworkClient.api.uploadPipeline(pipeline)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun downloadPipeline(id: String): PipelineDefinition? {
        return try {
            NetworkClient.api.getPipeline(id)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    suspend fun getMyPipelines(): List<PipelineDefinition> {
        return try {
            NetworkClient.api.getMyPipelines()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}

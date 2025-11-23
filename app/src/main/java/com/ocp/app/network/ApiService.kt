package com.ocp.app.network

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Path
import com.ocp.shared.PipelineDefinition

interface ApiService {
    data class PluginDto(val id: String, val name: String, val description: String, val price: Double)

    @GET("plugins")
    suspend fun getPlugins(): List<PluginDto>

    @POST("pipelines")
    suspend fun uploadPipeline(@Body pipeline: PipelineDefinition): PipelineDefinition

    @GET("pipelines/{id}")
    suspend fun getPipeline(@Path("id") id: String): PipelineDefinition
    
    @GET("pipelines/my")
    suspend fun getMyPipelines(): List<PipelineDefinition>
}

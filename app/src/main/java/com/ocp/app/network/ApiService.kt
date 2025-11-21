package com.ocp.app.network

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @POST("/auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("/auth/register")
    suspend fun register(@Body request: RegisterRequest): LoginResponse

    @GET("/plugins")
    suspend fun getPlugins(): List<PluginDto>
}

data class LoginRequest(val email: String, val password: String)
data class RegisterRequest(val email: String, val username: String, val password: String)
data class LoginResponse(val token: String, val user: UserDto)
data class UserDto(val id: String, val username: String, val email: String)
data class PluginDto(val id: String, val name: String, val description: String, val price: Double)

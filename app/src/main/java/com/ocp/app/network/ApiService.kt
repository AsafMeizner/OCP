package com.ocp.app.network

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
data class PluginDto(val id: String, val name: String, val description: String, val price: Double)

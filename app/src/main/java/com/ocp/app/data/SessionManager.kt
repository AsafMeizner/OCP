package com.ocp.app.data

object SessionManager {
    var authToken: String? = null
    
    fun saveToken(token: String) {
        authToken = token
        // In a real app, save to EncryptedSharedPreferences
    }
    
    fun getToken(): String? {
        return authToken
    }
    
    fun isLoggedIn(): Boolean {
        return authToken != null
    }
    
    fun logout() {
        authToken = null
    }
}

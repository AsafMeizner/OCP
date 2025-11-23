package com.ocp.app.data

object SessionManager {
    private const val PREF_NAME = "ocp_session"
    private const val KEY_TOKEN = "auth_token"
    
    private var prefs: android.content.SharedPreferences? = null
    var authToken: String? = null
        private set

    fun init(context: android.content.Context) {
        prefs = context.getSharedPreferences(PREF_NAME, android.content.Context.MODE_PRIVATE)
        authToken = prefs?.getString(KEY_TOKEN, null)
    }
    
    fun saveToken(token: String) {
        authToken = token
        prefs?.edit()?.putString(KEY_TOKEN, token)?.apply()
    }
    
    fun getToken(): String? {
        return authToken
    }
    
    fun isLoggedIn(): Boolean {
        return authToken != null
    }
    
    fun logout() {
        authToken = null
        prefs?.edit()?.remove(KEY_TOKEN)?.apply()
    }
}

package com.thejas.diamondgroup

import android.content.Context
import android.content.SharedPreferences

class SessionManager(private val context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)

    fun setLoggedIn(loggedIn: Boolean, username: String) {
        val editor = prefs.edit()
        editor.putBoolean("logged_in", loggedIn)
        editor.putString("username", username)
        editor.apply()
    }

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean("logged_in", false)
    }

    fun getUsername(): String? {
        return prefs.getString("username", null)
    }

    fun logout() {
        val editor = prefs.edit()
        editor.clear()
        editor.apply()
    }
}

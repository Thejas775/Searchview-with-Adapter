package com.thejas.diamondgroup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import com.google.android.material.button.MaterialButton

class LoginScreen : AppCompatActivity() {
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_screen)

        var username = findViewById<EditText>(R.id.username)
        var password = findViewById<EditText>(R.id.password)
        var btn = findViewById<MaterialButton>(R.id.loginbtn)
        sessionManager = SessionManager(this)


        btn.setOnClickListener {
            val enteredUsername = username.text.toString()
            val enteredPassword = password.text.toString()

            if (enteredUsername.isEmpty()) {
                Toast.makeText(this, "Enter Username", Toast.LENGTH_SHORT).show()
            } else if (enteredPassword.isEmpty()) {
                Toast.makeText(this, "Enter Password", Toast.LENGTH_SHORT).show()
            } else if (enteredUsername == "admin" && enteredPassword=="password") {
                sessionManager.setLoggedIn(true)
                val intent = Intent(this@LoginScreen, MainActivity2::class.java)
                startActivity(intent)
            }
        }
    }
}

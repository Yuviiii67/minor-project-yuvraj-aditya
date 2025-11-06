package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Find all the views from the layout file
        val emailInput: TextInputEditText = findViewById(R.id.emailInput)
        val passwordInput: TextInputEditText = findViewById(R.id.passwordInput)
        val loginButton: Button = findViewById(R.id.loginButton)
        val signUpText: TextView = findViewById(R.id.signUpText)

        // --- Login Button Click ---
        // We will just add a simple check and then open MainActivity
        loginButton.setOnClickListener {
            val email = emailInput.text.toString()
            val password = passwordInput.text.toString()

            // A very simple login check (you can make this more complex later)
            if (email.isNotEmpty() && password.isNotEmpty()) {
                // Login is "successful", navigate to the main app
                Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish() // Close the login activity so "back" doesn't return here
            } else {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            }
        }

        // --- Sign Up Text Click ---
        signUpText.setOnClickListener {
            // For now, just show a message.
            // Later, you would create a SignUpActivity and launch it here.
            Toast.makeText(this, "Sign Up page coming soon!", Toast.LENGTH_SHORT).show()
        }
    }
}
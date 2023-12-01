package com.example.weproject

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignUpActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        findViewById<Button>(R.id.create)?.setOnClickListener {
            val userEmail = findViewById<EditText>(R.id.user_email)?.text.toString()
            val password = findViewById<EditText>(R.id.user_password)?.text.toString()
            doSignup(userEmail, password)
        }
    }
    private fun doSignup(userEmail: String, password: String) {
        Firebase.auth.createUserWithEmailAndPassword(userEmail, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Firebase.auth.signInWithEmailAndPassword(userEmail, password)

                    startActivity(Intent(this, MainActivityBottomNav::class.java))
                    println("로그인 성공했덩ㅇ 나이떠")
                    finish()
                    startActivity(Intent(this, MainActivity::class.java))

                } else {
                    Log.w("LoginActivity", "createUserWithEmail", task.exception)
                    Toast.makeText(this, "Account creation failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
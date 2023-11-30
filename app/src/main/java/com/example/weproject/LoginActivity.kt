package com.example.weproject

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        println("???? 나이떠")
        findViewById<Button>(R.id.login)?.setOnClickListener {
            val userEmail = findViewById<EditText>(R.id.username)?.text.toString()
            val password = findViewById<EditText>(R.id.password)?.text.toString()
            doLogin(userEmail, password)
        }
        findViewById<Button>(R.id.signup)?.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }


    private fun doLogin(userEmail: String, password: String) {

        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("로그인 중...")
        progressDialog.setCancelable(false) // 사용자가 다이얼로그를 취소할 수 없도록 설정
        progressDialog.show()
        Firebase.auth.signInWithEmailAndPassword(userEmail, password)
            .addOnCompleteListener(this) {
                progressDialog.dismiss()
                if (it.isSuccessful) {
                    startActivity(Intent(this, MainActivityBottomNav::class.java))
                    println("로그인 성공했덩ㅇ 나이떠")
                    finish()
                    startActivity(Intent(this, MainActivity::class.java))
                } else {
                    Log.w("LoginActivity", "signInWithEmail", it.exception)
                    Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
package com.example.weproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.ui.AppBarConfiguration
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val currentUser = Firebase.auth.currentUser
        println("현재 사용자: $currentUser")
        if (currentUser == null) {
            println("로그인 화면을 켜야했더")
            startActivity(Intent(this, LoginActivity::class.java))
            //finish()
        } else {
            setContentView(R.layout.activity_main_bottom_nav)

            val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNav)
            bottomNavigationView.visibility = View.VISIBLE

            val homeFragment = HomeFragment()
            val chatFragment = ChatRoomFragment()
            val myPageFragment = MyPageFragment()

            bottomNavigationView.setOnNavigationItemSelectedListener { item ->
                // 여기서 로그인 여부를 확인하고, 로그인되어 있지 않으면 해당 아이템 선택을 무시

                if (Firebase.auth.currentUser != null) {
                    when (item.itemId) {
                        R.id.home -> replaceFragment(homeFragment)
                        R.id.chat -> replaceFragment(chatFragment)
                        R.id.myPage -> replaceFragment(myPageFragment)

                    }
                    return@setOnNavigationItemSelectedListener true
                } else {
                    // 로그인되어 있지 않은 경우에는 해당 아이템 선택 무시
                    println("로그인 안되었덩")
                    startActivity(Intent(this, LoginActivity::class.java))
                    return@setOnNavigationItemSelectedListener false
                }
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .apply {
                replace(R.id.fragment, fragment)
                commit()
            }
    }
}

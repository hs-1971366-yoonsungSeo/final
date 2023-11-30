package com.example.weproject

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weproject.databinding.ActivityMylistBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize

class MyListActivity : AppCompatActivity() {
    private var adapter: ArticleAdapter? = null
    private lateinit var articleDB: DatabaseReference
    private val progressWait by lazy { findViewById<ProgressBar>(R.id.progressWait) }
    private var binding: ActivityMylistBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_mylist)

        binding = ActivityMylistBinding.inflate(layoutInflater)
        // 레이아웃이 null이 아닌지 확인
        binding?.let { safeBinding ->
            setContentView(safeBinding.root)

            // Firebase 초기화
            Firebase.initialize(this)

            val currentUser = Firebase.auth.currentUser?.email.toString()

            // Realtime Database에서 데이터 가져오기
            articleDB = FirebaseDatabase.getInstance().reference.child("articles")
            if (Firebase.auth.currentUser != null) {//사용자 아이디가 같은 리스트만 출력하기
                queryDataByUser(currentUser)
            }

            adapter = ArticleAdapter {
            }


        }
    }
    private fun queryDataByUser(currentUser: String) {
        progressWait.visibility = View.VISIBLE

        // Realtime Database에서 전체 데이터 가져오기

        articleDB.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                progressWait.visibility = View.GONE
                // 가져온 데이터를 ArticleModel 리스트로 변환
                val items = mutableListOf<ArticleModel>()

                for (articleSnapshot in dataSnapshot.children) {
                    if (articleSnapshot.child("sellerId").value.toString() == currentUser) {
                        val sellerId = articleSnapshot.child("sellerId").value.toString()
                        val title = articleSnapshot.child("title").value.toString()
                        val content = articleSnapshot.child("content").value.toString()
                        val createdAt = articleSnapshot.child("createdAt").value as Long
                        val price = articleSnapshot.child("price").value.toString()
                        val imageURL = articleSnapshot.child("imageURL").value.toString()
                        val onSale = articleSnapshot.child("onSale").value.toString()
                        val articleModel = ArticleModel(sellerId, title, content, createdAt, price, imageURL,onSale,true)
                        items.add(articleModel)
                    }

                }

                // RecyclerView에 데이터 설정


                binding?.articleRecyclerView?.adapter = adapter

                // 리사이클러뷰 레이아웃 매니저 설정
                binding?.articleRecyclerView?.layoutManager = LinearLayoutManager(this@MyListActivity)

                // 아이템 리스트를 어댑터에 전달

                adapter?.submitList(items)
                adapter?.notifyDataSetChanged()

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}
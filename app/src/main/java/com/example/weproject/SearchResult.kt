package com.example.weproject

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weproject.databinding.ActivityResultBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize

class SearchResult : AppCompatActivity() {
    private var adapter: ArticleAdapter? = null
    private lateinit var articleDB: DatabaseReference
    private val progressWait by lazy { findViewById<ProgressBar>(R.id.progressWait) }
    private var binding: ActivityResultBinding? = null
    val currentUser = Firebase.auth.currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)

        // 레이아웃이 null이 아닌지 확인
        binding?.let { safeBinding ->
            setContentView(safeBinding.root)

            // Firebase 초기화
            Firebase.initialize(this)

            val searchQuery = intent.getStringExtra("searchQuery")

            // Realtime Database에서 데이터 가져오기
            articleDB = FirebaseDatabase.getInstance().reference.child("articles")
            //articleDB = FirebaseDatabase.getInstance().getReference("/articles")
            if (searchQuery != null) {
                queryDataByName(searchQuery)
            }
        }
    }

    private fun queryDataByName(searchQuery: String) {
        progressWait.visibility = View.VISIBLE

        // Realtime Database에서 전체 데이터 가져오기
        //articleDB.orderByChild("title").equalTo(searchQuery).addChildEventListener(object : ChildEventListener {

        articleDB.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                progressWait.visibility = View.GONE
                // 가져온 데이터를 ArticleModel 리스트로 변환
                val items = mutableListOf<ArticleModel>()

                for (articleSnapshot in dataSnapshot.children) {
                    if (articleSnapshot.child("title").value.toString() == searchQuery) {
                        val sellerId = articleSnapshot.child("sellerId").value.toString()
                        val title = articleSnapshot.child("title").value.toString()
                        val content = articleSnapshot.child("content").value.toString()
                        val createdAt = articleSnapshot.child("createdAt").value as Long
                        val price = articleSnapshot.child("price").value.toString()
                        val imageURL = articleSnapshot.child("imageURL").value.toString()
                        val onSale= articleSnapshot.child("onSale").value.toString()

                        val articleModel = ArticleModel(sellerId, title,content, createdAt, price, imageURL,onSale,false)
                        items.add(articleModel)
                    }

                }

                // RecyclerView에 데이터 설정
                adapter = ArticleAdapter { clickedArticleModel ->
                    println("아이템 클릭됨")
                    if (currentUser?.email == clickedArticleModel.sellerId) {
                        // 사용자가 게시한 글에 대해서만 UpdateActivity로 이동
                        val intent = Intent(this@SearchResult, UpdateActivity::class.java)
                        intent.putExtra("createdAt", clickedArticleModel.createdAt)
                        intent.putExtra("title", clickedArticleModel.title)
                        intent.putExtra("price", clickedArticleModel.price)
                        intent.putExtra("imageURL", clickedArticleModel.imageURL)
                        intent.putExtra("onSale", clickedArticleModel.onSale)
                        startActivity(intent)
                    } else {
                        // 사용자가 게시한 글이 아니면 상세 페이지로 이동
                        val intent = Intent(this@SearchResult, PostDetailActivity::class.java)
                        intent.putExtra("postDetails", clickedArticleModel)
                        startActivity(intent)
                    }
                }

                binding?.articleRecyclerView?.adapter = adapter

                // 리사이클러뷰 레이아웃 매니저 설정
                binding?.articleRecyclerView?.layoutManager = LinearLayoutManager(this@SearchResult)

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
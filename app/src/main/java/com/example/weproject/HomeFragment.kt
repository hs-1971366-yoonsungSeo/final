package com.example.weproject

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weproject.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var articleDB: DatabaseReference
    private lateinit var userDB: DatabaseReference
    private lateinit var articleAdapter: ArticleAdapter
    val currentUser = Firebase.auth.currentUser
    private val articleList = mutableListOf<ArticleModel>()
    private lateinit var articleModel: ArticleModel

    private val listener = object : ChildEventListener {
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            val articleModel = snapshot.getValue(ArticleModel::class.java)
            articleModel ?: return

            articleList.add(0, articleModel)
            articleAdapter.submitList(articleList.toList())
        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

        override fun onChildRemoved(snapshot: DataSnapshot) {}

        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

        override fun onCancelled(error: DatabaseError) {}
    }

    private var binding: FragmentHomeBinding? = null
    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }

    private var filteredArticleList = mutableListOf<ArticleModel>()

    private val radioGroup: RadioGroup by lazy {
        binding?.radioGroup2 ?: requireView().findViewById(R.id.radioGroup2)
    }

    private val onSaleRadioButton: RadioButton by lazy {
        binding?.onsale ?: requireView().findViewById(R.id.onsale)
    }

    private val doneRadioButton: RadioButton by lazy {
        binding?.done ?: requireView().findViewById(R.id.done)
    }

    private val allRadioButton: RadioButton by lazy {
        binding?.all ?: requireView().findViewById(R.id.all)
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        println("홈화면임")

        articleList.clear()

        val fragmentHomeBinding = FragmentHomeBinding.bind(view)
        binding = fragmentHomeBinding

        articleDB = Firebase.database.reference.child("articles")


        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            filteredArticleList.clear()

            when (checkedId) {
                R.id.onsale -> filterArticles("판매중")
                R.id.done -> filterArticles("판매완료")
                R.id.all -> filterArticles("") // 모든 아이템 표시
            }

            articleAdapter.submitList(filteredArticleList.toList())
        }


        articleAdapter = ArticleAdapter { clickedArticleModel ->
            println("아이템 클릭됨")
            if (currentUser?.email == clickedArticleModel.sellerId) {
                // 사용자가 게시한 글에 대해서만 UpdateActivity로 이동
                val intent = Intent(requireContext(), UpdateActivity::class.java)
                intent.putExtra("createdAt", clickedArticleModel.createdAt)
                intent.putExtra("title", clickedArticleModel.title)
                intent.putExtra("price", clickedArticleModel.price)
                intent.putExtra("imageURL", clickedArticleModel.imageURL)
                intent.putExtra("onSale",clickedArticleModel.onSale)
                startActivity(intent)
            } else {
                // 사용자가 게시한 글이 아니면 상세 페이지로 이동
                val intent = Intent(requireContext(), PostDetailActivity::class.java)
                intent.putExtra("postDetails", clickedArticleModel)
                startActivity(intent)
            }
        }

        binding?.articleRecyclerView?.adapter = articleAdapter
        binding?.articleRecyclerView?.layoutManager = LinearLayoutManager(context)

        fragmentHomeBinding.addFloatingButton.setOnClickListener {
            context?.let {
                val intent = Intent(it, FloatArticleActivity::class.java)
                startActivity(intent)
            }
        }
        fragmentHomeBinding.searchFloatingButton.setOnClickListener {
            val intent = Intent(requireContext(), SearchActivity::class.java)
            startActivity(intent)
        }

        articleDB.addChildEventListener(listener)
    }

    override fun onResume() {
        super.onResume()
        articleAdapter.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        articleDB.removeEventListener(listener)
    }

    private fun filterArticles(status: String) {
        for (article in articleList) {
            if (article.onSale == status || status.isEmpty()) {
                filteredArticleList.add(article)
            }
        }
    }
}
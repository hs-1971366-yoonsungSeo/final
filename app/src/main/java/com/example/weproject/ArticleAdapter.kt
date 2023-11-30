package com.example.weproject

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weproject.databinding.ItemArticleBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class ArticleAdapter(val onItemClicked: (ArticleModel) -> Unit): ListAdapter<ArticleModel, ArticleAdapter.ViewHolder>(diffUtil) {

    // 뷰홀더 클래스 정의
    inner class ViewHolder(private val binding: ItemArticleBinding): RecyclerView.ViewHolder(binding.root) {

        private val statusTextView: TextView = binding.status
        // 뷰홀더에 데이터를 바인딩하는 함수
        fun bind(articleModel: ArticleModel) {
            // 날짜 형식 지정을 위한 포맷터
            val format = SimpleDateFormat("MM월 dd일")
            val date = Date(articleModel.createdAt)
            val priceFormat = DecimalFormat("#,###")

            // 아이템 데이터를 뷰에 설정
            binding.titleTextView.text = articleModel.title
            binding.dateTextView.text = format.format(date).toString()
            binding.priceTextView.text = "${priceFormat.format(articleModel.price.toInt())}원"
            statusTextView.text = articleModel.onSale
            if(articleModel.visible==false) {
                binding.update.visibility= View.GONE
                binding.delete.visibility= View.GONE
            } else {
                binding.update.visibility=View.VISIBLE
                binding.delete.visibility=View.VISIBLE
            }

            // 이미지 URL이 비어있지 않은 경우 Glide를 사용하여 이미지 로드
            if (articleModel.imageURL.isNotEmpty()) {
                Glide.with(binding.thumbnailImageView)
                    .load(articleModel.imageURL)
                    .into(binding.thumbnailImageView)
            } else {
                Glide.with(binding.thumbnailImageView).load(R.drawable.carrot1).into(binding.thumbnailImageView)
            }

            // 아이템 클릭 이벤트 처리
            binding.root.setOnClickListener {
                onItemClicked(articleModel)
            }
            binding.update.setOnClickListener {
                val articleModel = getItem(adapterPosition)
                updateItem(articleModel.createdAt)
            }
            binding.delete.setOnClickListener {
                var articleModel=getItem(adapterPosition)
                deleteItem(articleModel.createdAt)
            }
        }
        private fun updateItem(createdAt: Long) {
            try {
                // 수정할 아이템의 인덱스를 가져옴
                val position = getIndexOfItem(createdAt)
                if (position != -1) {
                    // 수정할 아이템을 가져옴
                    val updatedItem = currentList[position].copy(visible = !currentList[position].visible)
                    println("여기까지")
                    // Intent를 사용하여 UpdateActivity 시작
                    val intent = Intent(binding.root.context, UpdateActivity::class.java)
                    intent.putExtra("createdAt", createdAt)
                    intent.putExtra("title", updatedItem.title)
                    intent.putExtra("price", updatedItem.price)
                    intent.putExtra("imageURL", updatedItem.imageURL)
                    intent.putExtra("onSale", updatedItem.onSale)
                    println("여기도옴?")
                    binding.root.context.startActivity(intent)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                println("예외발생")
            }
        }

        private fun deleteItem(createdAt: Long) {
            try {
                val articleDB = Firebase.database.reference.child("articles")
                val articleRef = articleDB.orderByChild("createdAt").equalTo(createdAt.toDouble())

                articleRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            for (articleSnapshot in snapshot.children) {
                                articleSnapshot.ref.removeValue()
                            }
                            navigateToHomeFragment()
                        } else {
                            println("해당 아이템을 찾을 수 없습니다.")
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        println("아이템 삭제 중 오류가 발생했습니다: ${error.message}")
                    }
                })
            } catch (e: Exception) {
                e.printStackTrace()
                println("예외 발생")
            }
        }
        private fun navigateToHomeFragment() {
            val intent = Intent(binding.root.context, MainActivity::class.java)
            binding.root.context.startActivity(intent)

        }

    }
    // 뷰홀더를 생성하는 함수
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemArticleBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    // 뷰홀더에 데이터를 바인딩하는 함수
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val articleModel = getItem(position)
        holder.bind(articleModel)
    }

    // 아이템이 같은지 비교하는 유틸리티
    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<ArticleModel>() {
            override fun areItemsTheSame(oldItem: ArticleModel, newItem: ArticleModel): Boolean {
                return oldItem.createdAt == newItem.createdAt
            }

            override fun areContentsTheSame(oldItem: ArticleModel, newItem: ArticleModel): Boolean {
                return oldItem == newItem
            }
        }
    }
    fun getIndexOfItem(createdAt: Long): Int {
        return currentList.indexOfFirst { it.createdAt == createdAt }
    }


}

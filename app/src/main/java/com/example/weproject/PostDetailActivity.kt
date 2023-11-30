package com.example.weproject;

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date

class PostDetailActivity : AppCompatActivity() {
    private lateinit var postDetails: ArticleModel
    private val db = FirebaseFirestore.getInstance()
    private val chatsCollection: CollectionReference = db.collection("chats")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)

        postDetails = intent.getParcelableExtra("postDetails") ?: return
        println(postDetails.toString())
        // XML 레이아웃에서 각 뷰를 찾아와서 게시글 정보를 설정합니다.
        val thumbnailImageView = findViewById<ImageView>(R.id.thumbnail1ImageView)
        val titleTextView = findViewById<TextView>(R.id.title1TextView)
        val dateTextView = findViewById<TextView>(R.id.date1TextView)
        val contentTextView = findViewById<TextView>(R.id.content1TextView)
        val priceTextView = findViewById<TextView>(R.id.price1TextView)
        val sellerTextView = findViewById<TextView>(R.id.sellerTextView)
        val onSaleTextView = findViewById<TextView>(R.id.onSaleTextView)
        val chatButton = findViewById<Button>(R.id.button2)

        chatButton.setOnClickListener {
            createOrChatRoom()
        }
        // Glide나 다른 이미지 로딩 라이브러리를 사용하여 이미지 로드


        titleTextView.text =  postDetails.title
        dateTextView.text = SimpleDateFormat("MM월 dd일").format(Date(postDetails.createdAt))
        priceTextView.text = "${NumberFormat.getInstance().format(postDetails.price.toInt())}원"
        sellerTextView.text =  postDetails.sellerId
        contentTextView.text = postDetails.content
        onSaleTextView.text = postDetails.onSale


    }
    private fun createOrChatRoom(){
        val currentUser = Firebase.auth.currentUser?.email.toString()
        chatsCollection
            .whereEqualTo("title", postDetails.title)
            .get()
            .addOnSuccessListener { documents ->
                if(documents.isEmpty){
                    createNewChatRoom()
                } else {
                    val chatRoomId = documents.documents[0].id
                    chatIfSenderMatch(chatRoomId,currentUser)

                }
            }
    }
    private fun createNewChatRoom(){
        val currentUser = Firebase.auth.currentUser?.email
        val chatRoom = hashMapOf(
            "title" to postDetails.title,
            "sellerId" to postDetails.sellerId,
            "senderId" to currentUser
        )
        chatsCollection
            .add(chatRoom)
            .addOnSuccessListener { documentReference ->
                val chatRoomId = documentReference.id

                val initialMessage = hashMapOf(
                    "senderId" to currentUser
                )

                db.collection("chats")
                    .document(chatRoomId)
                    .collection("messages")
                    .add(initialMessage)
                    .addOnSuccessListener {
                        openChatRoom(chatRoomId)
                    }
            }
    }

    private fun chatIfSenderMatch(chatRoomId: String, currentUser: String){
        db.collection("chats")
            .document(chatRoomId)
            .collection("messages")
            .whereEqualTo("senderId", currentUser)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    createNewChatRoom()
                } else {
                    openChatRoom(chatRoomId)
                }
            }
    }
    private fun openChatRoom(chatRoomId: String){
        val sellerId = postDetails.sellerId
        val title = postDetails.title

        val intent = Intent(this@PostDetailActivity, ChatActivity::class.java)
        intent.putExtra("chatRoomId", chatRoomId)
        intent.putExtra("sellerId", sellerId)
        intent.putExtra("title", title)
        startActivity(intent)
    }

}
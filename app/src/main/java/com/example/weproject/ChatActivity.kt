package com.example.weproject

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weproject.databinding.FragmentChatBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.ktx.Firebase

class ChatActivity : AppCompatActivity(R.layout.fragment_chat) {
    private lateinit var binding: FragmentChatBinding
    private val db = FirebaseFirestore.getInstance()    // Firestore 인스턴스
    private lateinit var registration: ListenerRegistration    // 문서 수신
    private val chatList = arrayListOf<ChatLayout>()    // 리사이클러 뷰 목록
    private lateinit var adapter: ChatAdapter
    private lateinit var chatRoomId: String
    private lateinit var postDetails: ArticleModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        chatRoomId = intent.getStringExtra("chatRoomId") ?: ""
        // 리사이클러 뷰 설정
        binding.rvList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        adapter = ChatAdapter(chatList)
        binding.rvList.adapter = adapter

        // 채팅창이 공백일 경우 버튼 비활성화
        binding.etChatting.addTextChangedListener { text ->
            binding.btnSend.isEnabled = text.toString() != ""
        }

        // 입력 버튼
        binding.btnSend.setOnClickListener {
            // 입력 데이터
            val contents = binding.etChatting.text.toString()
            if (contents.isNotEmpty()) {
                sendMessage(contents)
            }
        }

        loadPreviousMessages()

        registration = db.collection("chats")
            .document(chatRoomId)
            .collection("messages")
            .orderBy("timestamp")
            .addSnapshotListener { snapshots, e ->
                // 오류 발생 시
                if (e != null) {
                    Log.w("ChatActivity", "Listen failed: $e")
                    return@addSnapshotListener
                }

                // 원하지 않는 문서 무시
                if (snapshots!!.metadata.isFromCache) return@addSnapshotListener

                chatList.clear()
                // 문서 수신
                for (doc in snapshots) {
                    val senderId = doc.getString("senderId") ?: ""
                    val contents = doc.getString("contents") ?: ""
                    chatList.add(ChatLayout(senderId, contents))
                }
                adapter.notifyDataSetChanged()
            }
    }

    private fun loadPreviousMessages() {
        // 이전 메시지 불러오기
        db.collection("chats")
            .document(chatRoomId)
            .collection("messages")
            .orderBy("timestamp")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val senderId = document.getString("senderId") ?: ""
                    val contents = document.getString("contents") ?: ""
                    chatList.add(ChatLayout(senderId, contents))
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.e("ChatActivity", "Error getting documents: ", exception)
            }
    }

    private fun sendMessage(contents: String){
        val currentUser = Firebase.auth.currentUser?.email
        val sellerId = intent.getStringExtra("sellerId") ?: ""
        val title = intent.getStringExtra("title") ?: ""

        val messageData = hashMapOf(
            "senderId" to currentUser,  // Replace with the actual sender's ID
            "contents" to contents,
            "timestamp" to FieldValue.serverTimestamp(),
            "sellerId" to sellerId,
            "title" to title
        )

        db.collection("chats")
            .document(chatRoomId)
            .collection("messages")
            .add(messageData)
            .addOnSuccessListener {
                binding.etChatting.text.clear()
                Log.d("ChatActivity", "Message sent successfully")
            }
            .addOnFailureListener { e ->
                Log.e("ChatActivity", "Error sending message", e)
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        registration.remove()
    }
}


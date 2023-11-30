package com.example.weproject

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weproject.databinding.FragmentChatRoomBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class ChatRoomFragment : Fragment(R.layout.fragment_chat_room) {
    private lateinit var binding: FragmentChatRoomBinding
    private val db = FirebaseFirestore.getInstance()
    private lateinit var adapter: ChatRoomAdapter
    private val chatRooms = arrayListOf<ChatRoomLayout>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentChatRoomBinding.bind(view)

        // Initialize RecyclerView
        adapter = ChatRoomAdapter(chatRooms)
        binding.rvChatRoom.layoutManager = LinearLayoutManager(requireContext())
        binding.rvChatRoom.adapter = adapter


        loadChatRooms()
    }

    private fun loadChatRooms() {
        val currentUser = Firebase.auth.currentUser?.email
        db.collection("chats")
            .whereEqualTo("sellerId", currentUser)
            .get()
            .addOnSuccessListener { documents ->
                chatRooms.clear()
                for (document in documents) {
                    val sellerId = document.getString("sellerId") ?: ""
                    val senderId = document.getString("senderId") ?: ""
                    val articleTitle = document.getString("title") ?: ""

                    db.collection("chats")
                        .document(document.id)
                        .collection("messages")
                        .orderBy("timestamp")
                        .get()
                        .addOnSuccessListener { messages ->
                            for(document in messages){
                                val messageContent = document.getString("contents") ?: ""
                                val chatRoomLayout = ChatRoomLayout(senderId, sellerId, articleTitle, messageContent)
                                chatRooms.add(chatRoomLayout)
                            }

                            adapter.notifyDataSetChanged()
                        }
                }

            }
            .addOnFailureListener { exception ->
                Log.e("ChatRoomFragment", "Error getting chat rooms", exception)
            }
    }
}




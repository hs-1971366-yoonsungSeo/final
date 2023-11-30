package com.example.weproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ChatRoomAdapter(val chatRooms: List<ChatRoomLayout>) :
    RecyclerView.Adapter<ChatRoomAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chat_room, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return chatRooms.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chatRoom = chatRooms[position]
        holder.senderIdTextView.text = "구매 희망자: ${chatRoom.senderId}"
        holder.articleTitleTextView.text = "글 제목: ${chatRoom.articleTitle}"
        holder.messageContentTextView.text = "메세지 내용: ${chatRoom.messageContent}"
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val senderIdTextView: TextView = itemView.findViewById(R.id.senderIdTextView)
        val articleTitleTextView: TextView = itemView.findViewById(R.id.articleTitleTextView)
        val messageContentTextView: TextView = itemView.findViewById(R.id.messageContentTextView)
    }
}

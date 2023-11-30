package com.example.weproject

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class ChatAdapter(val itemList: ArrayList<ChatLayout>): RecyclerView.Adapter<ChatAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.chat_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ChatAdapter.ViewHolder, position: Int) {
        holder.card.setCardBackgroundColor(Color.parseColor("#6365FF"))
        holder.contents.text = itemList[position].contents
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val card: CardView = itemView.findViewById(R.id.chat_card_view)
        val contents: TextView = itemView.findViewById(R.id.chat_tv_contents)
    }
}
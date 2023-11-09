package com.cofbro.qian.friend.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cofbro.qian.databinding.ItemMessageListBinding
import com.cofbro.qian.friend.viewholder.MessageListContentViewHolder

class MessageListAdapter : RecyclerView.Adapter<MessageListContentViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageListContentViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemMessageListBinding.inflate(inflater, parent, false)
        return MessageListContentViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return 50
    }

    override fun onBindViewHolder(holder: MessageListContentViewHolder, position: Int) {
        holder.bind(position)
    }
}
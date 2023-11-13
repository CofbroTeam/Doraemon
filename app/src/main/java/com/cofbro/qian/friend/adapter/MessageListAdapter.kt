package com.cofbro.qian.friend.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.fastjson.JSONObject
import com.cofbro.qian.databinding.ItemMessageListBinding
import com.cofbro.qian.friend.viewholder.MessageListContentViewHolder

class MessageListAdapter : RecyclerView.Adapter<MessageListContentViewHolder<JSONObject>>() {
    private var conversations = mutableListOf<JSONObject>()
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MessageListContentViewHolder<JSONObject> {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemMessageListBinding.inflate(inflater, parent, false)
        return MessageListContentViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return conversations.size
    }

    override fun onBindViewHolder(holder: MessageListContentViewHolder<JSONObject>, position: Int) {
        holder.bind(position, conversations.getOrNull(position))
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(conv: List<JSONObject>) {
        conversations = ArrayList(conv)
        notifyDataSetChanged()
    }

    fun insertData(conv: JSONObject, position: Int) {
        conversations.add(position, conv)
        notifyItemInserted(0)
    }

    fun insertBeforeFirst(conv: JSONObject) {
        insertData(conv, 0)
    }
}
package com.cofbro.qian.friend.viewholder

import androidx.recyclerview.widget.LinearLayoutManager
import com.cofbro.qian.databinding.ItemContentBinding
import com.cofbro.qian.friend.adapter.MessageListAdapter

class MessageListViewHolder(private val binding: ItemContentBinding) :
    DefaultViewHolder(binding) {
    private var contentAdapter: MessageListAdapter? = null
    override fun bind(position: Int) {
        contentAdapter = MessageListAdapter()
        binding.rvContent.apply {
            adapter = contentAdapter
            layoutManager = LinearLayoutManager(binding.root.context, LinearLayoutManager.VERTICAL, false)
        }
    }
}
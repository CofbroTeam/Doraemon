package com.cofbro.qian.friend.viewholder

import androidx.recyclerview.widget.LinearLayoutManager
import com.cofbro.qian.databinding.ItemContentBinding
import com.cofbro.qian.friend.adapter.UserListAdapter

class UserListViewHolder(private val binding: ItemContentBinding) : DefaultViewHolder(binding) {
    private var contentAdapter: UserListAdapter? = null
    override fun bind(position: Int) {
        contentAdapter = UserListAdapter()
        binding.rvContent.apply {
            adapter = contentAdapter
            layoutManager = LinearLayoutManager(binding.root.context, LinearLayoutManager.HORIZONTAL, false)
        }
    }
}
package com.cofbro.qian.friend.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cofbro.qian.databinding.ItemFriendListBinding
import com.cofbro.qian.friend.viewholder.UserListContentViewHolder

class UserListAdapter: RecyclerView.Adapter<UserListContentViewHolder>()  {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserListContentViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding =  ItemFriendListBinding.inflate(inflater, parent, false)
        return UserListContentViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return 16
    }

    override fun onBindViewHolder(holder: UserListContentViewHolder, position: Int) {
        holder.bind(position)
    }
}
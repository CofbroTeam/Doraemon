package com.cofbro.qian.friend.search.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.fastjson.JSONObject
import com.amap.api.services.help.Tip
import com.cofbro.qian.databinding.ItemSearchfrendsListBinding
import com.cofbro.qian.friend.viewholder.FriendsViewHolder

class FriendsAdapter(private val itemclick: (Friends?) -> Unit): RecyclerView.Adapter<FriendsViewHolder<Friends>>() {
     var currentFriends: MutableList<Friends>  = mutableListOf()
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FriendsViewHolder<Friends> {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemSearchfrendsListBinding.inflate(inflater, parent, false)
        return FriendsViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: FriendsViewHolder<Friends>,
        position: Int
    ) {
        holder.bind(position, currentFriends.getOrNull(position))
        holder.setItemClickListener {
            itemclick.invoke(currentFriends.getOrNull(position))
        }
    }
    override fun getItemCount(): Int {
        return currentFriends.size
    }


}
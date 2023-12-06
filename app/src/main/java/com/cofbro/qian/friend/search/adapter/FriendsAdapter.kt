package com.cofbro.qian.friend.search.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amap.api.services.help.Tip
import com.cofbro.qian.databinding.ItemSearchfrendsListBinding
import com.cofbro.qian.friend.viewholder.FriendsViewHolder

class FriendsAdapter(val context: Context, private val currentFriends: MutableList<Friends>,private val itemclick: (Friends) -> Unit): RecyclerView.Adapter<FriendsViewHolder<Friends>>() {
    private var itemClick: (() -> Unit?)? = null
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
        holder.bind(position, currentFriends[position])
        holder.setItemClickListener {
            itemclick.invoke(it)
        }
    }

    override fun getItemCount(): Int {
        return currentFriends.size
    }


}
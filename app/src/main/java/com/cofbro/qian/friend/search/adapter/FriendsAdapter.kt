package com.cofbro.qian.friend.search.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cofbro.qian.databinding.ItemSearchfrendsListBinding
import com.cofbro.qian.friend.viewholder.FriendsViewHolder

class FriendsAdapter (val context: Context, private val currentFriends:MutableList<Friends>): RecyclerView.Adapter<FriendsViewHolder<Friends>>() {
    private var itemClick: ((itemTip: Friends) -> Unit?)? = null
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FriendsViewHolder<Friends> {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemSearchfrendsListBinding.inflate(inflater, parent, false)
        return FriendsViewHolder(binding,this)
    }

    override fun onBindViewHolder(
        holder: FriendsViewHolder<Friends>,
        position: Int
    ) {
        holder.bind(position,currentFriends[position])
    }

    override fun getItemCount(): Int {
        return currentFriends.size
    }
    fun setItemClickListener(itemClickListener: (itemTip: Friends) -> Unit){
        itemClick = itemClickListener
    }
}
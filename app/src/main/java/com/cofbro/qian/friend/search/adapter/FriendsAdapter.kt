package com.cofbro.qian.friend.search.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cofbro.qian.databinding.ItemSearchfrendsListBinding

class FriendsAdapter (val context: Context, private val currentFriends:MutableList<Friends>): RecyclerView.Adapter<FriendsAdapter.FriendsViewHolder>() {
    private var itemClick: ((itemTip: Friends) -> Unit?)? = null
    class FriendsViewHolder(private val binding: ItemSearchfrendsListBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int, currentTip:MutableList<Friends>, itemClick: ((itemTip: Friends) -> Unit?)? ){
            binding.friendicon.apply {
                Glide.with(this.context)
                    .load(currentTip[position].src)
                    .into(this)
            }
            binding.friendname.text = currentTip[position].name
            binding.addfriend.setOnClickListener {
                itemClick?.invoke(currentTip[position])
            }
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FriendsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemSearchfrendsListBinding.inflate(inflater, parent, false)
        return FriendsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FriendsViewHolder, position: Int) {
        holder.bind(position,currentFriends,itemClick)
    }

    override fun getItemCount(): Int {
        return currentFriends.size
    }
    fun setItemClickListener(itemClickListener: (itemTip: Friends) -> Unit){
        itemClick = itemClickListener
    }
}
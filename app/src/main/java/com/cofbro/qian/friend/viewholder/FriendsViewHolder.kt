package com.cofbro.qian.friend.viewholder
import com.bumptech.glide.Glide
import com.cofbro.qian.databinding.ItemSearchfrendsListBinding
import com.cofbro.qian.friend.search.adapter.Friends
import com.cofbro.qian.friend.search.adapter.FriendsAdapter

class FriendsViewHolder<T: Friends>(private val binding: ItemSearchfrendsListBinding, private val adapter: FriendsAdapter) :
DefaultViewHolder<T>(binding) {
    override fun bind(position: Int, t: T?) {
        binding.friendicon.apply {
            Glide.with(this.context)
                .load(t?.src)
                .into(this)
        }
        binding.friendname.text = t?.name
        binding.addfriend.setOnClickListener {
           /*
           添加用户
            */
        }
    }
}
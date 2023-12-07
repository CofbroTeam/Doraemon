package com.cofbro.qian.friend.viewholder

import android.view.View
import cn.leancloud.LCObject
import com.bumptech.glide.Glide
import com.cofbro.qian.databinding.ItemFriendListBinding
import com.cofbro.qian.databinding.ItemSearchfrendsListBinding
import com.cofbro.qian.friend.search.adapter.Friends
import com.cofbro.qian.friend.search.adapter.FriendsAdapter

class FriendsViewHolder<T : Friends>(private val binding: ItemSearchfrendsListBinding) :
    DefaultViewHolder<T>(binding) {
    private var itemClick: (() -> Unit?)? = null
    override fun bind(position: Int, t: T?) {
        // 头像
        setImage(binding.ivFriendIcon, t?.src)
        // username
        binding.tvFriendName.text = t?.name
        // tip
        binding.tvFriendTip.text = if (t?.isfriend == true) "老朋友~" else "新朋友~"
        binding.tvAddFriend.apply {
            if (t?.isfriend == true) {
                visibility = View.GONE
            } else {
                setItemClickListener {
                    itemClick?.invoke()
                }
            }
        }
    }

    fun setItemClickListener(itemClickListener: () -> Unit) {
        itemClick = itemClickListener
    }
}
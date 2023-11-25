package com.cofbro.qian.friend.viewholder

import cn.leancloud.LCObject
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.cofbro.qian.databinding.ItemFriendListBinding
import com.cofbro.qian.friend.im.IMClientUtils
import com.cofbro.qian.utils.dp2px

class UserListContentViewHolder<T : LCObject>(private val binding: ItemFriendListBinding) :
    DefaultViewHolder<T>(binding) {
    override fun bind(position: Int, t: T?) {
        innerBind(t)
    }

    private fun innerBind(t: T?) {
        val url = t?.getString("url") ?: ""
        val name = t?.getString("name") ?: ""
        setImage(binding.ivAvatar, url)
        binding.tvUsername.text = name
    }
}
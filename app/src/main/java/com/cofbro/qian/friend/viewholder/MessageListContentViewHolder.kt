package com.cofbro.qian.friend.viewholder

import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.cofbro.qian.R
import com.cofbro.qian.databinding.ItemMessageListBinding
import com.cofbro.qian.utils.dp2px

class MessageListContentViewHolder(private val binding: ItemMessageListBinding) : DefaultViewHolder(binding) {
    override fun bind(position: Int) {
        val options = RequestOptions().transform(
            CenterCrop(),
            RoundedCorners(dp2px(binding.root.context, 25))
        )
        Glide.with(binding.root)
            .load(R.drawable.duolaa)
            .apply(options)
            .into(binding.ivAvatar)
    }
}
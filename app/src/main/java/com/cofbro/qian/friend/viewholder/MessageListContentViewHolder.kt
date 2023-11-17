package com.cofbro.qian.friend.viewholder

import android.content.Intent
import android.view.View
import com.alibaba.fastjson.JSONObject
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.cofbro.qian.databinding.ItemMessageListBinding
import com.cofbro.qian.friend.chatActivity.ChatActivity
import com.cofbro.qian.utils.dp2px
import com.cofbro.qian.utils.getIntExt
import com.cofbro.qian.utils.getLongExt
import com.cofbro.qian.utils.getStringExt
import com.cofbro.qian.utils.safeParseToJson

class MessageListContentViewHolder<T : JSONObject>(private val binding: ItemMessageListBinding) :
    DefaultViewHolder<T>(binding) {
    override fun bind(position: Int, t: T?) {
        loadImage(t?.getStringExt("avatar") ?: "")
        binding.tvUsername.text = t?.getStringExt("username") ?: ""
        binding.tvLastMessage.text =
            t?.getStringExt("content")?.safeParseToJson()?.getStringExt("_lctext")
        binding.tvTime.text = formatTimestamp(t?.getLongExt("time") ?: 0L)

        val unReadCount = t?.getIntExt("unReadCount") ?: 0
        bindUnReadMsgCount(unReadCount)

        itemView.setOnClickListener {
            binding.root.context.startActivity(Intent(binding.root.context, ChatActivity::class.java))
        }
    }

    private fun bindUnReadMsgCount(unReadCount: Int) {
        if (unReadCount > 0) {
            binding.tvUnreadMsgCount.text = unReadCount.toString()
            binding.tvUnreadMsgCount.visibility = View.VISIBLE
        } else {
            binding.tvUnreadMsgCount.visibility = View.GONE
        }
    }

    private fun loadImage(url: String) {
        if (url.isEmpty()) return
        val options = RequestOptions().transform(
            CenterCrop(),
            RoundedCorners(dp2px(binding.root.context, 25))
        )
        Glide.with(binding.root)
            .load(url)
            .apply(options)
            .into(binding.ivAvatar)
    }
}
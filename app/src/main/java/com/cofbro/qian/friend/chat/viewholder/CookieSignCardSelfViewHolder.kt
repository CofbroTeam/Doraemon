package com.cofbro.qian.friend.chat.viewholder

import cn.leancloud.im.v2.LCIMMessage
import cn.leancloud.im.v2.messages.LCIMTextMessage
import com.cofbro.qian.data.URL
import com.cofbro.qian.databinding.ItemChatCookieSignCardMyselfBinding
import com.cofbro.qian.friend.viewholder.DefaultViewHolder
import com.cofbro.qian.utils.CacheUtils

class CookieSignCardSelfViewHolder(private val binding: ItemChatCookieSignCardMyselfBinding) :
    DefaultViewHolder<LCIMMessage>(binding) {
    override fun bind(position: Int, t: LCIMMessage?) {
        // 文字内容
        val content = (t as? LCIMTextMessage)?.text
        binding.tvCookieCardContent.text = content
        // 时间
        binding.tvTime.text = formatTimestamp(t?.timestamp ?: 0L)
        // 头像
        val avatar = URL.getAvtarImgPath(CacheUtils.cache["uid"] ?: "")
        setImage(binding.avatar, avatar)
    }
}
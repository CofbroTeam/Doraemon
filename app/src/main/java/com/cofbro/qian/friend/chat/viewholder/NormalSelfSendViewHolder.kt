package com.cofbro.qian.friend.chat.viewholder

import cn.leancloud.im.v2.LCIMMessage
import com.cofbro.qian.data.URL
import com.cofbro.qian.databinding.ItemChatNormalMyselfBinding
import com.cofbro.qian.friend.viewholder.DefaultViewHolder
import com.cofbro.qian.utils.CacheUtils
import com.cofbro.qian.utils.getStringExt
import com.cofbro.qian.utils.safeParseToJson

class NormalSelfSendViewHolder(private val binding: ItemChatNormalMyselfBinding) : DefaultViewHolder<LCIMMessage>(binding) {
    override fun bind(position: Int, t: LCIMMessage?) {
        // 消息
        binding.tvMessage.text = t?.content?.safeParseToJson()?.getStringExt("_lctext") ?: ""
        // 时间
        binding.tvTime.text = formatTimestamp(t?.timestamp ?: 0L)
        // 头像
        setImage(binding.avatar, URL.getAnalysisPath(CacheUtils.cache["uid"] ?: ""))
    }
}
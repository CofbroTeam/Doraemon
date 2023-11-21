package com.cofbro.qian.friend.viewholder

import android.content.Intent
import android.view.View
import cn.leancloud.im.v2.LCIMConversation
import com.alibaba.fastjson.JSONObject
import com.cofbro.qian.databinding.ItemMessageListBinding
import com.cofbro.qian.friend.adapter.MessageListAdapter
import com.cofbro.qian.friend.chat.ChatActivity
import com.cofbro.qian.utils.CacheUtils
import com.cofbro.qian.utils.Constants
import com.cofbro.qian.utils.getIntExt
import com.cofbro.qian.utils.getLongExt
import com.cofbro.qian.utils.getStringExt
import com.cofbro.qian.utils.safeParseToJson

class MessageListContentViewHolder<T : JSONObject>(private val binding: ItemMessageListBinding, private val adapter: MessageListAdapter) :
    DefaultViewHolder<T>(binding) {
    override fun bind(position: Int, t: T?) {
        val url = t?.getStringExt("avatar") ?: ""
        setImage(binding.ivAvatar, url)

        val username = t?.getStringExt("username") ?: ""
        binding.tvUsername.text = username
        binding.tvLastMessage.text =
            t?.getStringExt("content")?.safeParseToJson()?.getStringExt("_lctext")
        binding.tvTime.text = formatTimestamp(t?.getLongExt("time") ?: 0L)

        val unReadCount = t?.getIntExt("unReadCount") ?: 0
        bindUnReadMsgCount(unReadCount)

        val conv = t?.get("conv") as LCIMConversation

        itemView.setOnClickListener {
            refreshReadCount(position, t)
            toChatActivity(url, username, conv)
        }
    }

    private fun refreshReadCount(position: Int, t: T?) {
        t?.set("unReadCount", 0)
        adapter.notifyItemChanged(position)
    }

    private fun toChatActivity(url: String, username: String, conv: LCIMConversation) {
        val intent = Intent(binding.root.context, ChatActivity::class.java)
        intent.putExtra("avatar", url)
        intent.putExtra("username", username)
        CacheUtils.conv[Constants.Cache.CONV] = conv
        binding.root.context.startActivity(intent)
    }

    private fun bindUnReadMsgCount(unReadCount: Int) {
        if (unReadCount > 0) {
            binding.tvUnreadMsgCount.text = unReadCount.toString()
            binding.tvUnreadMsgCount.visibility = View.VISIBLE
        } else {
            binding.tvUnreadMsgCount.visibility = View.GONE
        }
    }
}
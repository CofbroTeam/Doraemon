package com.cofbro.qian.friend.viewholder

import android.content.Intent
import cn.leancloud.LCObject
import cn.leancloud.im.v2.LCIMConversation
import com.cofbro.qian.databinding.ItemFriendListBinding
import com.cofbro.qian.friend.adapter.UserListAdapter
import com.cofbro.qian.friend.chat.ChatActivity
import com.cofbro.qian.utils.CacheUtils
import com.cofbro.qian.utils.Constants
import com.cofbro.qian.utils.getStringExt

class UserListContentViewHolder<T : LCObject>(
    private val binding: ItemFriendListBinding,
    private val adapter: UserListAdapter
) :
    DefaultViewHolder<T>(binding) {
    override fun bind(position: Int, t: T?) {
        innerBind(position, t)
    }

    private fun innerBind(position: Int, t: T?) {
        val url = t?.getString("url") ?: ""
        val name = t?.getString("name") ?: ""
        setImage(binding.ivAvatar, url)
        binding.tvUsername.text = name

        itemView.setOnClickListener {
            val conv = findValidConv(t)
            conv?.let {
                toChatActivity(url, name, conv, position)
            }
        }
    }

    private fun findValidConv(t: T?): LCIMConversation? {
        val objectId = t?.getString("uid")
        adapter.getMessageConv()?.forEach {convJOSNObject ->
            val conversation = convJOSNObject["conv"] as? LCIMConversation
            conversation?.members?.forEach {
                if (it == objectId) {
                    return conversation
                }
            }
        }
        return null
    }


    private fun toChatActivity(url: String, username: String, conv: LCIMConversation, pos: Int) {
        val intent = Intent(binding.root.context, ChatActivity::class.java)
        intent.putExtra("avatar", url)
        intent.putExtra("username", username)
        intent.putExtra("pos", pos)
        CacheUtils.conv[Constants.Cache.CONV] = conv
        binding.root.context.startActivity(intent)
    }
}
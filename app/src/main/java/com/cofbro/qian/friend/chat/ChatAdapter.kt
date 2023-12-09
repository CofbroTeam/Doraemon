package com.cofbro.qian.friend.chat


import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cn.leancloud.im.v2.LCIMConversation
import cn.leancloud.im.v2.LCIMMessage
import cn.leancloud.im.v2.messages.LCIMTextMessage
import com.cofbro.qian.databinding.ItemChatCookieSignCardMyselfBinding
import com.cofbro.qian.databinding.ItemChatCookieSignCardUserSendBinding
import com.cofbro.qian.databinding.ItemChatNormalFriendBinding
import com.cofbro.qian.databinding.ItemChatNormalMyselfBinding
import com.cofbro.qian.friend.chat.viewholder.CookieSignCardSelfViewHolder
import com.cofbro.qian.friend.chat.viewholder.CookieSignCardUserViewHolder
import com.cofbro.qian.friend.chat.viewholder.NormalSelfSendViewHolder
import com.cofbro.qian.friend.chat.viewholder.NormalUserSendViewHolder
import com.cofbro.qian.friend.im.IMClientUtils
import com.cofbro.qian.friend.viewholder.DefaultViewHolder
import com.cofbro.qian.utils.Constants
import com.cofbro.qian.utils.MsgFactory


class ChatAdapter(val conv: LCIMConversation?, private val avatar: String) :
    RecyclerView.Adapter<DefaultViewHolder<LCIMMessage>>() {
    private var msgData = arrayListOf<LCIMMessage>()
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DefaultViewHolder<LCIMMessage> {
        val layoutInflater = LayoutInflater.from(parent.context)
        when (viewType) {
            // 自己发送的普通文字信息
            Constants.HolderType.NORMAL_SELF_SEND -> {
                val binding = ItemChatNormalMyselfBinding.inflate(layoutInflater, parent, false)
                return NormalSelfSendViewHolder(binding)
            }

            // 对方发送的普通文字信息
            Constants.HolderType.NORMAL_USER_SEND -> {
                val binding = ItemChatNormalFriendBinding.inflate(layoutInflater, parent, false)
                return NormalUserSendViewHolder(binding, avatar)
            }

            // 自己发送的cookie卡片
            Constants.HolderType.REQUEST_COOKIE_CARD_SELF_SEND -> {
                val binding = ItemChatCookieSignCardMyselfBinding.inflate(layoutInflater, parent, false)
                return CookieSignCardSelfViewHolder(binding)
            }

            // 用户发送的cookie卡片
            Constants.HolderType.REQUEST_COOKIE_CARD_USER_SEND -> {
                val binding = ItemChatCookieSignCardUserSendBinding.inflate(layoutInflater, parent, false)
                return CookieSignCardUserViewHolder(binding, this, avatar)
            }
        }
        return DefaultViewHolder(ItemChatNormalMyselfBinding.inflate(layoutInflater, parent, false))
    }

    override fun getItemCount(): Int {
        return msgData.size
    }

    override fun onBindViewHolder(holder: DefaultViewHolder<LCIMMessage>, position: Int) {
        holder.bind(position, msgData.getOrNull(position))
    }

    override fun getItemViewType(position: Int): Int {
        // 消息发送方
        val from = msgData[position].from
        // 卡片发送方
        val who = (msgData[position] as? LCIMTextMessage)?.attrs?.get(MsgFactory.who)
        return if (from == IMClientUtils.getCntUser()?.objectId) {
            if (who == from) {
                return Constants.HolderType.REQUEST_COOKIE_CARD_SELF_SEND
            }
            Constants.HolderType.NORMAL_SELF_SEND
        } else {
            if (who != null) {
                return Constants.HolderType.REQUEST_COOKIE_CARD_USER_SEND
            }
            Constants.HolderType.NORMAL_USER_SEND
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(data: List<LCIMMessage>) {
        msgData.addAll(data)
        notifyDataSetChanged()
    }

    fun insertData(data: List<LCIMMessage>, pos: Int) {
        msgData.addAll(pos, data)
        notifyItemRangeInserted(0, data.size)
    }

    fun replaceMsg(msg: LCIMMessage?, position: Int) {
        msg?.let {
            msgData.removeAt(position)
            msgData.add(position, msg)
            notifyItemChanged(position)
        }
    }
    fun insertMsg(msg: LCIMMessage?) {
        msg?.let {
            msgData.add(msg)
            notifyItemInserted(msgData.size - 1)
        }
    }

    fun insertDataAtFirst(data: List<LCIMMessage>) {
        insertData(data, 0)
    }

    fun getAllMsg(): List<LCIMMessage> {
        return msgData
    }

}
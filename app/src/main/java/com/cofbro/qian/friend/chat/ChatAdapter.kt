package com.cofbro.qian.friend.chat


import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cn.leancloud.im.v2.LCIMMessage
import com.cofbro.qian.databinding.ItemChatNormalFriendBinding
import com.cofbro.qian.databinding.ItemChatNormalMyselfBinding
import com.cofbro.qian.friend.chat.viewholder.NormalSelfSendViewHolder
import com.cofbro.qian.friend.chat.viewholder.NormalUserSendViewHolder
import com.cofbro.qian.friend.im.IMClientUtils
import com.cofbro.qian.friend.viewholder.DefaultViewHolder
import com.cofbro.qian.utils.Constants


class ChatAdapter(private val avatar: String) :
    RecyclerView.Adapter<DefaultViewHolder<LCIMMessage>>() {
    private var msgData = arrayListOf<LCIMMessage>()
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DefaultViewHolder<LCIMMessage> {
        val layoutInflater = LayoutInflater.from(parent.context)
        when (viewType) {
            Constants.HolderType.NORMAL_SELF_SEND -> {
                val binding = ItemChatNormalMyselfBinding.inflate(layoutInflater, parent, false)
                return NormalSelfSendViewHolder(binding)
            }

            Constants.HolderType.NORMAL_USER_SEND -> {
                val binding = ItemChatNormalFriendBinding.inflate(layoutInflater, parent, false)
                return NormalUserSendViewHolder(binding, avatar)
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
        val from = msgData[position].from
        return if (from == IMClientUtils.getCntUser()?.objectId) {
            Constants.HolderType.NORMAL_SELF_SEND
        } else {
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
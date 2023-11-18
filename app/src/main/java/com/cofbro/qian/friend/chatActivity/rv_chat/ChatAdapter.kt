package com.cofbro.qian.friend.chatActivity.rv_chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import cn.leancloud.LCObject
import com.cofbro.qian.databinding.ItemChatNormalFriendBinding
import com.cofbro.qian.databinding.ItemChatNormalMyselfBinding
import com.cofbro.qian.databinding.ItemChatRequestHomeworkFriendBinding
import com.cofbro.qian.databinding.ItemChatRequestHomeworkMyselfBinding
import com.cofbro.qian.friend.viewholder.DefaultViewHolder

class ChatAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var mItemList = emptyList<ChatContent>()
    fun setItemList(list: List<ChatContent>) {
        mItemList = list
    }

    fun getItemCounts() = mItemList.size

    private var onRejectClickListener: ((View) -> Unit)? = null
    fun setOnRejectClickListener(listener: (View) -> Unit) {
        onRejectClickListener = listener
    }

    private var onAcceptClickListener: ((View) -> Unit)? = null
    fun setOnAcceptClickListener(listener: (View) -> Unit) {
        onAcceptClickListener = listener
    }

    override fun getItemViewType(position: Int): Int {
        return mItemList[position].type
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding: ViewBinding
        when (viewType) {
            ChatContent.TYPE_NORMAL_MYSELF -> {
                binding = ItemChatNormalMyselfBinding.inflate(layoutInflater, parent, false)
                return NormalMyselfViewHolder(binding)
            }

            ChatContent.TYPE_NORMAL_FRIEND -> {
                binding = ItemChatNormalFriendBinding.inflate(layoutInflater, parent, false)
                return NormalFriendViewHolder(binding)
            }

            ChatContent.TYPE_REQUEST_HOMEWORK_MYSELF -> {
                binding =
                    ItemChatRequestHomeworkMyselfBinding.inflate(layoutInflater, parent, false)
                return RequestHomeWorkMyselfViewHolder(binding)
            }

            ChatContent.TYPE_REQUEST_HOMEWORK_FRIEND -> {
                binding =
                    ItemChatRequestHomeworkFriendBinding.inflate(layoutInflater, parent, false)
                return RequestHomeWorkFriendViewHolder(binding)
            }

            else -> {
                // 以上类型都不是，返回
                binding = ItemChatNormalMyselfBinding.inflate(layoutInflater, parent, false)
                return NormalMyselfViewHolder(binding)
            }
        }
    }

    override fun getItemCount(): Int {
        return mItemList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (mItemList[position].type) {
            ChatContent.TYPE_NORMAL_MYSELF -> {
                (holder as NormalMyselfViewHolder).bind(position, null)
            }

            ChatContent.TYPE_NORMAL_FRIEND -> {
                (holder as NormalFriendViewHolder).bind(position, null)
            }

            ChatContent.TYPE_REQUEST_HOMEWORK_MYSELF -> {
                (holder as RequestHomeWorkMyselfViewHolder).bind(position, null)
            }

            ChatContent.TYPE_REQUEST_HOMEWORK_FRIEND -> {
                (holder as RequestHomeWorkFriendViewHolder).bind(position, null)
            }
        }
    }

    inner class NormalMyselfViewHolder(private val binding: ItemChatNormalMyselfBinding) :
        DefaultViewHolder<LCObject>(binding) {
        override fun bind(position: Int, t: LCObject?) {
            binding.tvMessage.text = mItemList[position].message
            binding.tvTime.text = mItemList[position].time
        }
    }

    inner class NormalFriendViewHolder(private val binding: ItemChatNormalFriendBinding) :
        DefaultViewHolder<LCObject>(binding) {
        override fun bind(position: Int, t: LCObject?) {
            binding.tvMessage.text = mItemList[position].message
            binding.tvTime.text = mItemList[position].time
        }
    }

    inner class RequestHomeWorkMyselfViewHolder(private val binding: ItemChatRequestHomeworkMyselfBinding) :
        DefaultViewHolder<LCObject>(binding) {
        override fun bind(position: Int, t: LCObject?) {
            binding.tvMessage.text = mItemList[position].message
            binding.tvTime.text = mItemList[position].time
        }
    }

    inner class RequestHomeWorkFriendViewHolder(private val binding: ItemChatRequestHomeworkFriendBinding) :
        DefaultViewHolder<LCObject>(binding) {
        override fun bind(position: Int, t: LCObject?) {
            binding.apply {
                btnReject.setOnClickListener {
                    onRejectClickListener?.invoke(it)
                    btnReject.isClickable = false
                    btnAccept.isClickable = false
                    binding.btnAccept.alpha = 0.25f
                }
                btnAccept.setOnClickListener {
                    onAcceptClickListener?.invoke(it)
                    btnReject.isClickable = false
                    binding.btnReject.alpha = 0.25f
                }
            }
            binding.tvMessage.text = mItemList[position].message
            binding.tvTime.text = mItemList[position].time
        }
    }
}
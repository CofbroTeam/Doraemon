package com.cofbro.qian.friend.im.chatActivity

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.cofbro.qian.databinding.ItemChatNormalFriendBinding
import com.cofbro.qian.databinding.ItemChatNormalMyselfBinding
import com.cofbro.qian.databinding.ItemChatRequestHomeworkFriendBinding
import com.cofbro.qian.databinding.ItemChatRequestHomeworkMyselfBinding

class ChatAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var mItemList = emptyList<ChatContent>()
    fun setItemList(list: List<ChatContent>) {
        mItemList = list
    }

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
                throw IllegalStateException("ChatAdapter中，type类型不存在")
            }
        }
    }

    override fun getItemCount(): Int {
        return mItemList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolderBind)
            holder.bind(position)
    }

    inner class NormalMyselfViewHolder(private val binding: ItemChatNormalMyselfBinding) :
        RecyclerView.ViewHolder(binding.root), ViewHolderBind {
        override fun bind(position: Int) {
            binding.tvMessage.text = mItemList[position].message
            binding.tvTime.text = mItemList[position].time
        }
    }

    inner class NormalFriendViewHolder(private val binding: ItemChatNormalFriendBinding) :
        RecyclerView.ViewHolder(binding.root), ViewHolderBind {
        override fun bind(position: Int) {
            binding.tvMessage.text = mItemList[position].message
            binding.tvTime.text = mItemList[position].time
        }
    }

    inner class RequestHomeWorkMyselfViewHolder(private val binding: ItemChatRequestHomeworkMyselfBinding) :
        RecyclerView.ViewHolder(binding.root), ViewHolderBind {
        override fun bind(position: Int) {
            binding.tvMessage.text = mItemList[position].message
            binding.tvTime.text = mItemList[position].time
        }
    }

    inner class RequestHomeWorkFriendViewHolder(private val binding: ItemChatRequestHomeworkFriendBinding) :
        RecyclerView.ViewHolder(binding.root), ViewHolderBind {
        override fun bind(position: Int) {
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
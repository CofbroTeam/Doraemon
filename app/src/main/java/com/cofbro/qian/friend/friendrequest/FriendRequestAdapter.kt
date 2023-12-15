package com.cofbro.qian.friend.friendrequest

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.fastjson.JSONObject
import com.cofbro.qian.databinding.ItemMessageListBinding
import com.cofbro.qian.friend.viewholder.DefaultViewHolder
import com.cofbro.qian.utils.getBooleanExt
import com.cofbro.qian.utils.getStringExt

class FriendRequestAdapter : RecyclerView.Adapter<FriendRequestAdapter.FriendRequestViewHolder>() {
    private var data = mutableListOf<JSONObject>()
    private var onItemClick: ((Int) -> Unit)? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendRequestViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemMessageListBinding.inflate(inflater, parent, false)
        return FriendRequestViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: FriendRequestViewHolder, position: Int) {
        holder.bind(position, data.getOrNull(position))
    }

    inner class FriendRequestViewHolder(private val binding: ItemMessageListBinding) : DefaultViewHolder<JSONObject>(binding) {
        override fun bind(position: Int, t: JSONObject?) {
            val status = t?.getStringExt("status")
            val isCreator = t?.getBooleanExt("isCreator")
            if (isCreator == true) {
                binding.tvTime.text = status
                binding.tvLastMessage.text = "好友请求发送"
            } else {
                when (status) {
                    "agree" -> {
                        binding.tvTime.text = "已同意"
                    }
                    "notResponse" -> {
                        binding.tvTime.text = "同意"
                    }
                    else -> {
                        binding.tvTime.text = "已拒绝"
                    }
                }
                binding.tvLastMessage.text = t?.getString("content")
            }
            binding.tvUsername.text = t?.getStringExt("username")

            setImage(binding.ivAvatar, t?.getString("avatar"))

            binding.tvTime.setOnClickListener {
                onItemClick?.invoke(position)
            }
        }
    }

    fun setOnItemClickListener(listener: (Int) -> Unit) {
        onItemClick = listener
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(d: List<JSONObject>) {
        data = ArrayList(d)
        notifyDataSetChanged()
    }
}
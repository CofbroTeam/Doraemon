package com.cofbro.qian.friend.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.fastjson.JSONObject
import com.cofbro.qian.databinding.ItemContentBinding
import com.cofbro.qian.friend.viewholder.DefaultViewHolder
import com.cofbro.qian.friend.viewholder.MessageListViewHolder
import com.cofbro.qian.friend.viewholder.UserListViewHolder

class ContentAdapter : RecyclerView.Adapter<DefaultViewHolder>() {
    private var users: JSONObject? = null
    private var messages: JSONObject? = null
    object ViewHolderType {
        const val TYPE_USER_LIST = 100
        const val TYPE_MESSAGE_LIST = 101
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> ViewHolderType.TYPE_USER_LIST
            1 -> ViewHolderType.TYPE_MESSAGE_LIST
            else -> ViewHolderType.TYPE_MESSAGE_LIST
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DefaultViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == ViewHolderType.TYPE_USER_LIST) {
            val binding = ItemContentBinding.inflate(inflater, parent, false)
            UserListViewHolder(binding)
        } else  {
            val binding = ItemContentBinding.inflate(inflater, parent, false)
            MessageListViewHolder(binding)
        }
    }

    override fun getItemCount(): Int {
        return 2
    }

    override fun onBindViewHolder(holder: DefaultViewHolder, position: Int) {
        holder.bind(position)
    }


}
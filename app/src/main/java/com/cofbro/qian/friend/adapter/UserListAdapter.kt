package com.cofbro.qian.friend.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cn.leancloud.LCObject
import com.cofbro.qian.databinding.ItemFriendListBinding
import com.cofbro.qian.friend.viewholder.UserListContentViewHolder

class UserListAdapter : RecyclerView.Adapter<UserListContentViewHolder<LCObject>>() {
    private var users = arrayListOf<LCObject>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UserListContentViewHolder<LCObject> {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemFriendListBinding.inflate(inflater, parent, false)
        return UserListContentViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return users.size
    }

    override fun onBindViewHolder(holder: UserListContentViewHolder<LCObject>, position: Int) {
        holder.bind(position, users.getOrNull(position))
    }

    fun insertItemRange(data: List<LCObject>) {
        users.addAll(0, data)
        notifyItemRangeInserted(0, data.size)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(data: List<LCObject>) {
        users = ArrayList(data)
        notifyDataSetChanged()
    }

    fun insertData(user: LCObject) {
        users.add(0, user)
        notifyItemInserted(0)
    }

    fun getData(): ArrayList<LCObject> {
        return users
    }
}
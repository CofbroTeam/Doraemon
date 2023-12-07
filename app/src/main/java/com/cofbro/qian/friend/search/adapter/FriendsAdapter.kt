package com.cofbro.qian.friend.search.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cofbro.qian.databinding.ItemSearchfrendsListBinding
import com.cofbro.qian.friend.viewholder.FriendsViewHolder

class FriendsAdapter : RecyclerView.Adapter<FriendsViewHolder<Friends>>() {
    private var itemClickListener: ((Friends?) -> Unit)? = null
    private val data = arrayListOf<Friends>()
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FriendsViewHolder<Friends> {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemSearchfrendsListBinding.inflate(inflater, parent, false)
        return FriendsViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: FriendsViewHolder<Friends>,
        position: Int
    ) {
        holder.bind(position, data.getOrNull(position))
        holder.setItemClickListener {
            itemClickListener?.invoke(data.getOrNull(position))
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun setOnItemClickListener(listener: (Friends?) -> Unit) {
        itemClickListener = listener
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(t: List<Friends>?) {
        data.clear()
        t?.let {
            data.addAll(it)
        }
        notifyDataSetChanged()
    }

}
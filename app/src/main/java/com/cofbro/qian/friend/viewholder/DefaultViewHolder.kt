package com.cofbro.qian.friend.viewholder

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

open class DefaultViewHolder(private val layoutBinding: ViewBinding) : RecyclerView.ViewHolder(layoutBinding.root) {
    open fun bind(position: Int) {
    }
}
package com.cofbro.qian.friend.viewholder

import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import java.text.SimpleDateFormat
import java.util.Date

open class DefaultViewHolder<T>(private val layoutBinding: ViewBinding) : RecyclerView.ViewHolder(layoutBinding.root) {
    open fun bind(position: Int, t: T?) {
    }

    @SuppressLint("SimpleDateFormat")
    fun formatTimestamp(timestamp: Long): String {
        val date = Date(timestamp)
        val dateFormat = SimpleDateFormat("HH:mm")
        return dateFormat.format(date)
    }
}
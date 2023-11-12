package com.cofbro.qian.friend.viewholder

import android.annotation.SuppressLint
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.cofbro.qian.utils.dp2px
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

    fun setImage(view: ImageView?, url: String?) {
        if (url.isNullOrEmpty() || view == null) return
        val options = RequestOptions().transform(
            CenterCrop(),
            RoundedCorners(dp2px(layoutBinding.root.context, 25))
        )
        Glide.with(layoutBinding.root)
            .load(url)
            .apply(options)
            .into(view)
    }
}
package com.cofbro.qian.friend.viewholder

import android.annotation.SuppressLint
import android.content.Context
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


open class DefaultViewHolder<T>(private val layoutBinding: ViewBinding) :
    RecyclerView.ViewHolder(layoutBinding.root) {
    open fun bind(position: Int, t: T?) {
    }

    fun getHolderContext(): Context {
        return layoutBinding.root.context
    }

    @SuppressLint("SimpleDateFormat")
    fun formatTimestamp(timestamp: Long): String {
        var timeString = ""
        val nowTime = System.currentTimeMillis()
        val internalTime = 24 * 60 * 60 * 1000
        val date = Date(timestamp)
        val dateFormat = SimpleDateFormat("HH:mm")
        val todayDuration = acquireDurationFromMidnight()
        timeString = if (nowTime - timestamp >= internalTime + todayDuration) {
            SimpleDateFormat("MM-dd HH:mm").format(date)
        } else if (nowTime - timestamp < internalTime  + todayDuration
            && nowTime - timestamp >= todayDuration
        ) {
            "昨天 " + dateFormat.format(date)
        } else {
            dateFormat.format(date)
        }
        return timeString
    }

    @SuppressLint("SimpleDateFormat")
    fun acquireDurationFromMidnight(): Long {
        val time = Date()
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
        val today = simpleDateFormat.format(time.time)
        return System.currentTimeMillis() - (simpleDateFormat.parse(today)?.time ?: 0L)
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
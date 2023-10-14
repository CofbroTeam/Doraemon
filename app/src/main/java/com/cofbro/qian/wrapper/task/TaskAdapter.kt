package com.cofbro.qian.wrapper.task

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.fastjson.JSONObject
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.cofbro.qian.databinding.ItemTaskListBinding
import com.cofbro.qian.utils.Constants
import com.cofbro.qian.utils.dp2px
import com.cofbro.qian.utils.getJSONArrayExt
import com.cofbro.qian.utils.getStringExt

class TaskAdapter : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {
    private var data: JSONObject? = null
    private var itemClick: ((itemData: JSONObject) -> Unit)? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemTaskListBinding.inflate(inflater, parent, false)
        return TaskViewHolder(binding)
    }

    override fun getItemCount(): Int {
        if (data == null) return 0
        return (data?.getStringExt(Constants.TaskList.COUNT, "0") ?: "0").toInt()
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class TaskViewHolder(private val binding: ItemTaskListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            // 此item数据
            val item = data?.getJSONArrayExt(Constants.TaskList.ACTIVE_LIST)?.get(position)
            (item as? JSONObject)?.let {
                // 绑定活动图片
                val options = RequestOptions().transform(
                    CenterCrop(),
                    RoundedCorners(dp2px(binding.root.context, 5))
                )
                Glide.with(binding.root)
                    .load(item.getStringExt(Constants.TaskList.PIC_URL))
                    .apply(options)
                    .into(binding.ivTaskImage)

                binding.tvTitle.text = item.getStringExt(Constants.TaskList.TITLE, "-")

                val timeLine = item.getStringExt(Constants.TaskList.TIME_LINE, "进行中")
                binding.tvTimeLine.text = timeLine.takeIf { timeLine.isNotEmpty() } ?: "进行中"

                itemView.setOnClickListener {
                    itemClick?.invoke(item)
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(t: JSONObject) {
        data = t
        notifyDataSetChanged()
    }

    fun setItemClickListener(itemClickListener: (itemData: JSONObject) -> Unit) {
        itemClick = itemClickListener
    }
}
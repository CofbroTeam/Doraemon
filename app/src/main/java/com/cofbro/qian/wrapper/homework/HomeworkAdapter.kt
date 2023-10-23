package com.cofbro.qian.wrapper.homework

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.fastjson.JSONObject
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.cofbro.qian.R
import com.cofbro.qian.databinding.ItemHomeworkListBinding
import com.cofbro.qian.utils.dp2px

class HomeworkAdapter : RecyclerView.Adapter<HomeworkAdapter.HomeworkViewHolder>() {
    private var data: List<JSONObject>? = null
    private var itemClick: ((JSONObject) -> Unit)? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeworkViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemHomeworkListBinding.inflate(inflater, parent, false)
        return HomeworkViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return data?.size ?: 0
    }

    override fun onBindViewHolder(holder: HomeworkViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class HomeworkViewHolder(val binding: ItemHomeworkListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val itemData = data?.getOrNull(position) ?: JSONObject()
            val options = RequestOptions().transform(
                CenterCrop(),
                RoundedCorners(dp2px(binding.root.context, 5))
            )
            Glide.with(binding.ivHomework)
                .load(R.drawable.homework)
                .apply(options)
                .into(binding.ivHomework)
            binding.tvHomeworkStatus.text = itemData["status"].toString()
            binding.tvHomeworkTime.text = itemData["deadline"].toString()
            binding.tvHomeworkTitle.text = itemData["title"].toString()
            itemView.setOnClickListener {
                itemClick?.invoke(itemData)
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(t: List<JSONObject>) {
        data = t
        notifyDataSetChanged()
    }

    fun setItemClickListener(listener: (JSONObject) -> Unit) {
        itemClick = listener
    }
}
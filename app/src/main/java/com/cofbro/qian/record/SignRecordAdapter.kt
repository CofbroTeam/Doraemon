package com.cofbro.qian.record

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.fastjson.JSONObject
import com.cofbro.qian.R
import com.cofbro.qian.databinding.ItemRecordSignBinding
import com.cofbro.qian.utils.getIntExt
import com.cofbro.qian.utils.getJSONArrayExt
import com.cofbro.qian.utils.getStringExt
import com.cofbro.qian.utils.splitDateStr


/**
 * {
 *     "size": "2"
 *     "records": [
 *         {
 *             "username": "cofbro",
 *             "courseName": "数学",
 *             "time": "2023.10.31 11:00:00",
 *             "status": "success"
 *         },
 *         {
 *             "username": "cofbro",
 *             "courseName": "语文",
 *             "time": "2023.10.31 11:00:00",
 *             "status": "success"
 *         }
 *       ]
 * }
 */
class SignRecordAdapter : RecyclerView.Adapter<SignRecordAdapter.SignRecordViewHolder>() {
    private var recordData: JSONObject? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SignRecordViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemRecordSignBinding.inflate(inflater, parent, false)
        return SignRecordViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return recordData?.getIntExt("size") ?: 0
    }

    override fun onBindViewHolder(holder: SignRecordViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class SignRecordViewHolder(val binding: ItemRecordSignBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(position: Int) {
            val size = recordData?.getIntExt("size") ?: 0
            val record = recordData?.getJSONArrayExt("records")
                ?.getOrNull(size - position - 1) as? JSONObject
                ?: JSONObject()

            // uid
            binding.tvRecordUsername.text = "uid: ${record.getStringExt("uid")}"

            // 签到状态
            val status = record.getStringExt("status")
            binding.tvRecordStatus.text = status
            if (status == "失败") {
                binding.tvRecordStatus.setTextColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.red
                    )
                )
            }

            // 课程名称
            binding.tvRecordCourseName.text = record.getStringExt("courseName")

            // 绑定时间
            val dateStr = record.getStringExt("time")
            binding.tvRecordTime.text = dateStr
            val dateArray = splitDateStr(dateStr)
            binding.tvBookReocrdYear.text = dateArray[0]
            binding.tvBookReocrdMonth.text = dateArray[1]
            binding.tvBookReocrdDay.text = dateArray[2]
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(t: JSONObject?) {
        recordData = t
        notifyDataSetChanged()
    }


}
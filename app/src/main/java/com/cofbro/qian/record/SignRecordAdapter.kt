package com.cofbro.qian.record

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.fastjson.JSONObject
import com.cofbro.qian.R
import com.cofbro.qian.databinding.ItemRecordSignBinding
import com.cofbro.qian.utils.Constants
import com.cofbro.qian.utils.getIntExt
import com.cofbro.qian.utils.getJSONArrayExt
import com.cofbro.qian.utils.getStringExt
import com.cofbro.qian.utils.splitDateStr
import kotlin.random.Random


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
    private val colorMap = hashMapOf<String, Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SignRecordViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemRecordSignBinding.inflate(inflater, parent, false)
        return SignRecordViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return recordData?.getIntExt(Constants.Recorder.SIZE) ?: 0
    }

    override fun onBindViewHolder(holder: SignRecordViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class SignRecordViewHolder(val binding: ItemRecordSignBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(position: Int) {
            val size = recordData?.getIntExt(Constants.Recorder.SIZE) ?: 0
            val record = recordData?.getJSONArrayExt(Constants.Recorder.RECORDS)
                ?.getOrNull(size - position - 1) as? JSONObject
                ?: JSONObject()

            // uid
            binding.tvRecordUsername.text = "uid: ${record.getStringExt(Constants.Recorder.UID)}"

            // 签到状态
            val status = record.getStringExt(Constants.Recorder.STATUS)
            binding.tvRecordStatus.text = status
            bindTextColor(status)

            // 课程名称
            val courseName = record.getStringExt(Constants.Recorder.COURSE_NAME)
            binding.tvRecordCourseName.text = courseName

            // 绑定时间
            val dateStr = record.getStringExt(Constants.Recorder.TIME)
            binding.tvRecordTime.text = dateStr
            val dateArray = splitDateStr(dateStr)
            binding.tvBookReocrdYear.text = dateArray[0]
            binding.tvBookReocrdMonth.text = dateArray[1]
            binding.tvBookReocrdDay.text = dateArray[2]

            // 渲染颜色
            val color = getRandomColor(courseName)
            binding.ticketView.setTopicColor(color)
            binding.tvBookReocrdDay.setTextColor(color)
        }

        private fun bindTextColor(status: String) {
            if (status == "失败") {
                binding.tvRecordStatus.setTextColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.red
                    )
                )
            } else {
                binding.tvRecordStatus.setTextColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.white
                    )
                )
            }
        }

        private fun getRandomColor(courseName: String): Int {
            return if (colorMap.contains(courseName)) {
                colorMap[courseName] ?: 0
            } else {
                val random = Random(System.currentTimeMillis())
                val color = Color.rgb(
                    random.nextInt(206) + 50,
                    random.nextInt(206) + 50,
                    random.nextInt(206) + 50
                )
                colorMap[courseName] = color
                color
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(t: JSONObject?) {
        recordData = t
        notifyDataSetChanged()
    }


}
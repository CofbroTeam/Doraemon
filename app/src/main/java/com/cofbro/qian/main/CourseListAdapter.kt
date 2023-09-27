package com.cofbro.qian.main

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.fastjson.JSONObject
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.cofbro.qian.databinding.ItemCourseListBinding
import com.cofbro.qian.utils.getJSONArrayExt
import com.cofbro.qian.utils.getStringExt

class CourseListAdapter : RecyclerView.Adapter<CourseListAdapter.CourseListViewHolder>() {
    private var data: JSONObject? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemCourseListBinding.inflate(inflater, parent, false)
        return CourseListViewHolder(binding)
    }

    override fun getItemCount(): Int {
        data?.getJSONArray("channelList")?.let {
           return it.size
        }
        return 0
    }

    override fun onBindViewHolder(holder: CourseListViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class CourseListViewHolder(private val binding: ItemCourseListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            data?.getJSONArray("channelList")?.get(position)?.let {
                val jsonObject = (it as? JSONObject)
                val cpi = jsonObject?.getStringExt("cpi")
                val classId = jsonObject?.getStringExt("key")
                val studentCount = jsonObject?.getStringExt("content.studentcount")
                val className = jsonObject?.getStringExt("content.name")
                val itemArray = jsonObject?.getJSONArrayExt("content.course.data")
                if (itemArray?.size != 0) {
                    val item = (itemArray?.get(0) as? JSONObject)
                    val courseId = item?.getStringExt("id")
                    val courseName = item?.getStringExt("name")
                    val school = item?.getStringExt("schools", "未设置学校")
                    val teacherName = item?.getStringExt("teacherfactor")

                    // 绑定数据
                    binding.tvCourseId.text = courseId
                    binding.tvClassName.text = courseName
                    binding.tvClassId.text = classId
                    binding.tvSchoolName.text = school
                    binding.tvStudentCount.text = studentCount
                    binding.tvTeacherName.text = teacherName

                    Glide.with(binding.root)
                        .load(item?.getStringExt("imageurl"))
                        .transform(RoundedCorners(20))
                        .into(binding.ivClassAvtar)
                }
            }
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(t: JSONObject) {
        data = t
        notifyDataSetChanged()
    }
}
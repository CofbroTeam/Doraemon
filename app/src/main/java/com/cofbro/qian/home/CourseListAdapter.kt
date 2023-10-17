package com.cofbro.qian.home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.fastjson.JSONObject
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.cofbro.qian.databinding.ItemCourseListBinding
import com.cofbro.qian.utils.Constants
import com.cofbro.qian.utils.dp2px
import com.cofbro.qian.utils.getJSONArrayExt
import com.cofbro.qian.utils.getStringExt

class CourseListAdapter : RecyclerView.Adapter<CourseListAdapter.CourseListViewHolder>() {
    private var data: JSONObject? = null
    private var listener: AdapterListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemCourseListBinding.inflate(inflater, parent, false)
        return CourseListViewHolder(binding)
    }

    override fun getItemCount(): Int {
        data?.getJSONArray(Constants.CourseList.CHANNEL_LIST)?.let { array ->
            return (array.size - 1).takeIf { it >= 0 } ?: 0
        }
        return 0
    }

    override fun onBindViewHolder(holder: CourseListViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class CourseListViewHolder(private val binding: ItemCourseListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            // position + 1 -> 数据中的第一个item不是课程
            data?.getJSONArray(Constants.CourseList.CHANNEL_LIST)?.get(position + 1)?.let {
                // 数据下发的item
                val jsonObject = (it as? JSONObject)
                // cpi，后面签到需用此参数
                val cpi = jsonObject?.getStringExt(Constants.CourseList.CPI) ?: ""
                // 班级id
                val classId = jsonObject?.getStringExt(Constants.CourseList.KEY) ?: ""
                // 学生人数
                val studentCount = jsonObject?.getStringExt(Constants.CourseList.STUDENT_COUNT)
                // 班级名称
                val className = jsonObject?.getStringExt(Constants.CourseList.CLASS_NAME)
                val itemArray = jsonObject?.getJSONArrayExt(Constants.CourseList.DATA)
                if (itemArray?.size != 0) {
                    val item = (itemArray?.get(0) as? JSONObject)
                    // 课程id
                    val courseId = item?.getStringExt(Constants.CourseList.COURSE_ID) ?: ""
                    // 课程名称
                    val courseName = item?.getStringExt(Constants.CourseList.COURSE_NAME)
                    // 学校名称
                    val school = item?.getStringExt(Constants.CourseList.SCHOOLS, "未设置学校")
                    // 老师
                    val teacherName = item?.getStringExt(Constants.CourseList.TEACHER_NAME)

                    // 绑定数据
                    binding.tvCourseId.text = courseId
                    binding.tvClassName.text = courseName
                    binding.tvClassId.text = classId
                    binding.tvSchoolName.text = school
                    binding.tvStudentCount.text = studentCount
                    binding.tvTeacherName.text = teacherName
                    val options = RequestOptions().transform(
                        CenterCrop(),
                        RoundedCorners(dp2px(binding.root.context, 5))
                    )
                    Glide.with(binding.root)
                        .load(item?.getStringExt(Constants.CourseList.IMG_URL))
                        .apply(options)
                        .into(binding.ivClassAvtar)
                    itemView.setOnClickListener {
                        listener?.onItemClick(courseId, classId, cpi)
                    }
                }
            }
        }

    }

    fun setOnItemClickListener(listener: AdapterListener) {
        this.listener = listener
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(t: JSONObject) {
        data = t
        notifyDataSetChanged()
    }

    interface AdapterListener {
        fun onItemClick(courseId: String, classId: String, cpi: String)
    }
}
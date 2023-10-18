package com.cofbro.qian.home

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.fastjson.JSONObject
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.cofbro.hymvvmutils.base.BaseFragment
import com.cofbro.qian.data.URL
import com.cofbro.qian.databinding.FragmentHomeBinding
import com.cofbro.qian.main.CourseListAdapter
import com.cofbro.qian.utils.CacheUtils
import com.cofbro.qian.utils.Constants
import com.cofbro.qian.utils.dp2px
import com.cofbro.qian.utils.getStatusBarHeight
import com.cofbro.qian.utils.safeParseToJson
import com.cofbro.qian.wrapper.WrapperActivity
import com.hjq.toast.ToastUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : BaseFragment<HomeViewModel, FragmentHomeBinding>() {
    private var scrolledDx = 0
    private var targetScrollDx = 0
    private var mAdapter: CourseListAdapter? = null
    private var data: JSONObject? = null

    override fun onAllViewCreated(savedInstanceState: Bundle?) {
        initView()
        doNetwork()
        initObserver()
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (resultCode == RESULT_OK) {
//            val result = data?.getStringExtra("result")
//            // SIGNIN:aid=402742574&source=15&Code=402742574&enc=548DF0246153AF088E756B59F33BF3F4
//            val splitArray = result?.split("&")
//            splitArray?.let {
//                val aid = it[0].substringAfter("id")
//                val enc = it[2].substringAfter("enc")
//                signWithCamera(aid, enc)
//            }
//
//        }
//    }

    private fun initView() {
        // toolBar
        fitToolbar()

        // 课程论列表
        mAdapter = CourseListAdapter()
        targetScrollDx = dp2px(requireContext(), 76)
        binding?.rvCourseList?.apply {
            // 滑动监听
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    computeVerticalScrollOffset().let {
                        if (it > targetScrollDx) {
                            solidAppToolBar(255)
                            return
                        }
                        scrolledDx = it
                    }
                    solidAppToolBar(((scrolledDx.toFloat() / targetScrollDx.toFloat()) * 255).toInt())

                    Log.d(
                        "MainActivity",
                        "scrolledDx: $scrolledDx, dx: $dx, alpha: ${binding?.appToolBar?.background?.alpha}"
                    )
                }
            })

            onFlingListener = object : RecyclerView.OnFlingListener() {
                override fun onFling(velocityX: Int, velocityY: Int): Boolean {
                    Log.d("MainActivity", "onFling: $velocityY")
                    if (velocityY >= 1200) {
                        binding?.appToolBar?.background?.alpha = 255
                    }
                    return false
                }

            }

            // 增加间距
            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    val defaultPadding = dp2px(requireContext(), 15)
                    val toolbarHeight = binding?.appToolBar?.height ?: 0
                    if (parent.layoutManager?.getPosition(view) == 0) {
                        return outRect.set(
                            defaultPadding,
                            toolbarHeight + dp2px(requireContext(), 5),
                            defaultPadding,
                            defaultPadding
                        )
                    } else if (parent.getChildAdapterPosition(view) == state.getItemCount() - 1) {
                        return outRect.set(
                            defaultPadding,
                            0,
                            defaultPadding,
                            dp2px(requireContext(), 80)
                        )
                    }
                    return outRect.set(defaultPadding, 0, defaultPadding, defaultPadding)
                }
            })
        }

        /**
         * ToolBar
         */
        // 用户头像
        val uid = CacheUtils.cache["uid"]
        uid?.let {
            val options = RequestOptions().transform(
                CenterCrop(),
                RoundedCorners(dp2px(requireContext(), 5))
            )
            Glide.with(this)
                .load(URL.getAvtarImgPath(it))
                .apply(options)
                .into(binding!!.ivUserAvtar)
        }

        // 导航栏扫码签到
        binding?.ivScanBtn?.setOnClickListener {
            // TODO: 扫码之前需要预签到，预签到时机？

        }
    }

    private fun fitToolbar() {
        // 设置toolbar高度
        val params = binding?.appToolBar?.layoutParams
        params?.height = getStatusBarHeight(requireContext()) + dp2px(requireContext(), 45)
        binding?.appToolBar?.layoutParams = params

        // 设置toolbar透明度
        solidAppToolBar(0)
    }

    private fun initObserver() {
        // 课程列表
        viewModel.loadCourseListLiveData.observe(this) {
            lifecycleScope.launch(Dispatchers.IO) {
                val s = it.data?.body?.string()
                withContext(Dispatchers.Main) {
                    s?.let {
                        val jsonObject = it.safeParseToJson()
                        mAdapter?.setData(jsonObject)
                        data = jsonObject
                        binding?.rvCourseList?.adapter = mAdapter
                        binding?.rvCourseList?.layoutManager =
                            LinearLayoutManager(
                                requireContext(),
                                LinearLayoutManager.VERTICAL,
                                false
                            )
                        mAdapter?.setOnItemClickListener(object :
                            CourseListAdapter.AdapterListener {
                            override fun onItemClick(
                                courseId: String,
                                classId: String,
                                cpi: String
                            ) {
                                toWrapperActivity(courseId, classId, cpi)
                            }
                        })
                    }
                }
            }
        }

        // 签到
        viewModel.signLiveData.observe(this) {
            lifecycleScope.launch(Dispatchers.IO) {
                val data = it.data?.body?.string()
                Log.d("MainActivity", "initObserver: $data")
            }
            ToastUtils.show("签到成功!")
        }
    }

    private fun doNetwork() {
        // 加载课程列表
        viewModel.loadCourseList(URL.getAllCourseListPath())
    }

    private fun getDataItemCount(): Int {
        data?.getJSONArray(Constants.CourseList.CHANNEL_LIST)?.let { array ->
            return (array.size - 1).takeIf { it >= 0 } ?: 0
        }
        return 0
    }

    private fun solidAppToolBar(alpha: Int) {
        binding?.appToolBar?.background?.alpha = alpha
    }


    private fun signWithCamera(aid: String, enc: String) {
        val uid = CacheUtils.cache["uid"] ?: ""
//        val url = "https://mobilelearn.chaoxing.com/pptSign/stuSignajax?activeId=2000072435046&uid=$uid&enc=BC9662672047A2F2E4A607CC59762973&c=2000072435046&DB_STRATEGY=PRIMARY_KEY&STRATEGY_PARA=2000072435046"
        val url =
            "https://mobilelearn.chaoxing.com/pptSign/stuSignajax?activeId=2000072435046&enc=BC9662672047A2F2E4A607CC59762973&fid=0"
        Log.d("MainActivity", "url: $url")
        //viewModel.signWithCamera(URL.getSignWithCameraPath(aid, uid, enc))
        viewModel.signWithCamera(url)
        //191970813
        // https://mobilelearn.chaoxing.com/widget/sign/e?id=2000072435046&c=2000072435046&enc=BC9662672047A2F2E4A607CC59762973&DB_STRATEGY=PRIMARY_KEY&STRATEGY_PARA=id
    }

    private fun toWrapperActivity(courseId: String, classId: String, cpi: String) {
        val intent = Intent(requireActivity(), WrapperActivity::class.java)
        intent.apply {
            putExtra("courseId", courseId)
            putExtra("classId", classId)
            putExtra("cpi", cpi)
        }
        startActivity(intent)
    }
}

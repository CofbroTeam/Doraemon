package com.cofbro.qian.wrapper.homework

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.fastjson.JSONObject
import com.cofbro.hymvvmutils.base.BaseFragment
import com.cofbro.qian.data.URL
import com.cofbro.qian.databinding.FragmentHomeworkBinding
import com.cofbro.qian.utils.Constants
import com.cofbro.qian.utils.HtmlParser
import com.cofbro.qian.wrapper.WrapperActivity
import com.cofbro.qian.wrapper.did.DoHomeworkActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


class HomeworkFragment : BaseFragment<HomeworkViewModel, FragmentHomeworkBinding>() {
    private var title = ""
    private var mAdapter: HomeworkAdapter? = null
    private var activity: WrapperActivity? = null
    private var refreshing = false;
    override fun onAllViewCreated(savedInstanceState: Bundle?) {
        initArgs()
        initView()
        initObserver()
        doNetwork()
    }

    private fun initArgs() {
        activity = requireActivity() as WrapperActivity
    }

    private fun initView() {
        // rv
        mAdapter = HomeworkAdapter().apply {
            setItemClickListener {
                val url = it["url"].toString()
                title = it["title"].toString()
                if (url.isNotEmpty()) {
                    toDoHomework(url)
                }
            }
        }
        binding?.rvHomework?.apply {
            adapter = mAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }

        // refresh
        binding?.rflHomework?.apply {
            setOnRefreshListener {
                autoRefresh(0, 5000, 1f,false)
                refreshing = true
                doNetwork()
            }
        }
    }

    private fun initObserver() {
        viewModel.encLiveData.observe(this) {
            lifecycleScope.launch(Dispatchers.IO) {
                // 解析HTML得enc
                val workEnc = HtmlParser.findEnc(it.data?.body?.string() ?: "")
                // 作业列表，里面就有写作业界面的url
                requestForHomeworkList(workEnc)
                //
            }
        }

        viewModel.homeworkListHTML.observe(this) {
            lifecycleScope.launch(Dispatchers.IO) {
                val data = HtmlParser.parseHomeworkHTML(it.data?.body?.string() ?: "")
                withContext(Dispatchers.Main) {
                    if (refreshing) {
                        refreshing = false
                        binding?.rflHomework?.finishRefresh()
                    }
                    mAdapter?.setData(data)
                }
            }
        }

        viewModel.todoWorkLiveData.observe(this) {
            lifecycleScope.launch(Dispatchers.IO) {
                val data = it.data?.body?.string() ?: ""
                val json = HtmlParser.parsePostHomeworkUrl(data)
                withContext(Dispatchers.Main) {
                    toDoHomeworkActivity(json)
                }
            }
        }
    }


    private fun doNetwork() {
        activity?.let {
            //viewModel.queryHomeworkList("https://mooc1.chaoxing.com/mooc-ans/mooc2/work/task?courseId=237515217&classId=83807809&cpi=197696127&workId=29941278&answerId=52683919&enc=497eb7d24c417919a6180702d2d5f69e")
            //viewModel.queryHomeworkList("https://mooc1.chaoxing.com/mooc2/work/list?courseId=237515217&classId=83807809&cpi=197696127&ut=s&enc=d4a913c7c07aec123ece0a00a31d061d")
            // 得到enc
            lifecycleScope.launch(Dispatchers.IO) {
                requestForEnc()
            }
        }
    }

    private suspend fun requestForEnc() {
        val courseId = activity?.courseId ?: ""
        val classId = activity?.classId ?: ""
        val cpi = activity?.cpi ?: ""
        viewModel.requestForEnc(URL.getWorkEncPath(courseId, classId, cpi))
    }

    private suspend fun requestForHomeworkList(enc: String) {
        val courseId = activity?.courseId ?: ""
        val classId = activity?.classId ?: ""
        val cpi = activity?.cpi ?: ""
        viewModel.requestForHomeworkHTML(URL.getHomeworkListPath(courseId, classId, cpi, enc))
    }

    private fun toDoHomework(url: String) {
        viewModel.toDoHomework(url)
    }

    private fun toDoHomeworkActivity(jsonObject: JSONObject) {
        val intent = Intent(requireActivity(), DoHomeworkActivity::class.java)
        intent.putExtra(Constants.Work.SUBMIT_URL, jsonObject[Constants.Work.SUBMIT_URL].toString())
        intent.putExtra(Constants.Work.PREFIX_URL, jsonObject[Constants.Work.PREFIX_URL].toString())
        intent.putExtra(Constants.Work.WORK_TYPE, jsonObject[Constants.Work.WORK_TYPE].toString())
        intent.putExtra(Constants.Work.DESCRIPTION, jsonObject[Constants.Work.DESCRIPTION].toString())
        intent.putExtra(Constants.Work.TITLE, title)
        startActivity(intent)
    }

}
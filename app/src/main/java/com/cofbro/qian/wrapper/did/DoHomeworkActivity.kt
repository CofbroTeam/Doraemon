package com.cofbro.qian.wrapper.did

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.alibaba.fastjson.JSONObject
import com.cofbro.hymvvmutils.base.BaseActivity
import com.cofbro.qian.databinding.ActivityDoHomeworkBinding
import com.cofbro.qian.utils.Constants
import com.cofbro.qian.utils.Downloader
import com.cofbro.qian.utils.dp2px
import com.cofbro.qian.utils.getStatusBarHeight
import com.cofbro.qian.utils.getStringExt
import com.cofbro.qian.utils.safeParseToJson
import com.cofbro.qian.view.FullScreenDialog
import com.hjq.toast.ToastUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URLEncoder


class DoHomeworkActivity : BaseActivity<DoHomeworkViewModel, ActivityDoHomeworkBinding>() {
    private var objectId = ""
    private val resourceRequestCode = 100
    private var file: File? = null
    private var url = ""
    private var prefixPostUrl = ""
    private var description = ""
    private var type = ""
    private var title = ""
    private var hasFile = false
    private var loadingView: FullScreenDialog? = null
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        initArgs()
        iniView()
        initObserver()
    }

    private fun initObserver() {
        viewModel.submitHomeworkLiveData.observe(this) {
            lifecycleScope.launch(Dispatchers.IO) {
                val data = it.data?.body?.string()
                val jsonObject = data?.safeParseToJson()
                showResult(jsonObject)
                file?.delete()
            }
        }

        viewModel.attachFileLiveData.observe(this) {
            lifecycleScope.launch(Dispatchers.IO) {
                val data = it.data?.body?.string() ?: ""
                val jsonObject = data.safeParseToJson()
                objectId = jsonObject.getStringExt(Constants.Work.OBJECT_ID)
                withContext(Dispatchers.Main) {
                    showAttachedFile()
                    hideLoadingView()
                }
            }
        }
    }

    private fun iniView() {
        // appbar
        binding?.appToolBar?.apply {
            val height =
                getStatusBarHeight(this@DoHomeworkActivity) + dp2px(this@DoHomeworkActivity, 50)
            val layout = layoutParams
            layout.height = height
            layoutParams = layout
        }


        binding?.ivBack?.setOnClickListener {
            finish()
        }

        binding?.csAddFile?.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "*/*"
            startActivityForResult(intent, resourceRequestCode)
        }

        // 提交按钮
        binding?.btSubmit?.setOnClickListener {
            if (!hasFile) {
                val text = binding?.tvDoHomework?.text.toString()
                val codeText = "$prefixPostUrl%3Cp%3E${URLEncoder.encode(text, "UTF-8")}%3C%2Fp%3E"
                submitHomeworkWithRaw(url, codeText)
            } else {
                val text = prefixPostUrl + getPostUrl(
                    objectId,
                    file?.name ?: "",
                    Downloader.getFileType(file?.name ?: "")
                )
                submitHomeworkWithRaw(url, text)
            }

        }

        // 绑定数据
        binding?.tvWorkTitle?.text = title
        binding?.tvHomeworkType?.text = type
        binding?.tvDescription?.text = description
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == resourceRequestCode && resultCode == RESULT_OK) {
            if (data != null) {
                val uri = data.data
                uri?.let {
                    showLoadingView()
                    downloadFile(it)
                }
            }
        }
    }

    private fun initArgs() {
        url = intent.getStringExtra(Constants.Work.SUBMIT_URL) ?: ""
        prefixPostUrl = intent.getStringExtra(Constants.Work.PREFIX_URL) ?: ""
        description = intent.getStringExtra(Constants.Work.DESCRIPTION) ?: ""
        type = intent.getStringExtra(Constants.Work.WORK_TYPE) ?: ""
        title = intent.getStringExtra(Constants.Work.TITLE) ?: ""
    }

    private fun doNetwork() {

    }

    private fun submitHomeworkWithRaw(url: String, raw: String) {
        viewModel.submitHomework(url, raw)
    }

    private fun showResult(data: JSONObject?) {
        val success = data?.getStringExt("msg") ?: ""
        if (success.contains("success")) {
            ToastUtils.show("提交成功!")
        } else {
            ToastUtils.show("提交失败!")
        }
    }

    private fun getPostUrl(id: String, filename: String, fileType: String): String {
        val f = URLEncoder.encode(filename, "UTF-8")
        val text =
            "{\"objectid\":\"${id}\",\"name\":\"${filename}\",\"type\":\"$fileType\",\"size\":\"\"}"
        val urlEncodeEnc = URLEncoder.encode(text, "UTF-8")
        val textByte = urlEncodeEnc.toByteArray(Charsets.UTF_8)
        val bodyWithBase64 = String(Base64.encode(textByte, Base64.NO_WRAP))
        val final = bodyWithBase64.split("=").getOrNull(0) ?: bodyWithBase64
        return "%3Cp%3E%3Cbr%2F%3E%3C%2Fp%3E%3Cdiv+class%3D%22editor-iframe%22+contenteditable%3D%22false%22%3E%3Ciframe+frameborder%3D%220%22+scrolling%3D%22no%22+src%3D%22%2Fananas%2Fcommon-modules%2Fattachment%2FinsertCloud.html%22+class%3D%22attach-iframe%22+module%3D%22insertAttach%22+objectid%3D%22${id}%22+filename%3D%22${f}%22+filetype%3D%22${fileType}%22+filesize%3D%22%22+name%3D%22${final}%22+style%3D%22display%3A+block%3B+max-width%3A+620px%3B+width%3A+100%25%3B+height%3A+74px%3B%22%3E%3C%2Fiframe%3E%3C%2Fdiv%3E%3Cp%3E%3Cbr%2F%3E%3C%2Fp%3E%3Cp%3E%3Cbr%2F%3E%3C%2Fp%3E"
    }

    private fun downloadFile(uri: Uri) {
        lifecycleScope.launch(Dispatchers.IO) {
            Downloader.download(this@DoHomeworkActivity, uri) { success, filepath ->
                if (success) {
                    file = File(filepath)
                    viewModel.attachFile(url, file!!)
                    hasFile = true
                }
            }
        }
    }

    private fun showAttachedFile() {
        binding?.csAttachedFile?.visibility = View.VISIBLE
        binding?.tvFileTitle?.text = file?.name ?: ""
    }

    private fun showLoadingView() {
        if (loadingView == null) {
            loadingView = FullScreenDialog(this)
        }
        loadingView?.setCancelable(false)
        loadingView?.show()
    }

    private fun hideLoadingView() {
        loadingView?.dismiss()
        loadingView = null
    }


}
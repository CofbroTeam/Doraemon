package com.cofbro.qian.photo

import android.app.Dialog
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.alibaba.fastjson.JSONObject
import com.cofbro.hymvvmutils.base.BaseActivity
import com.cofbro.qian.data.URL
import com.cofbro.qian.databinding.ActivityPhotoSignBinding
import com.cofbro.qian.utils.CacheUtils
import com.cofbro.qian.utils.Constants
import com.cofbro.qian.utils.GlideEngine
import com.cofbro.qian.utils.Downloader
import com.cofbro.qian.utils.getStringExt
import com.cofbro.qian.utils.showSignResult
import com.cofbro.qian.view.dialog.FullScreenDialog
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.luck.picture.lib.utils.SandboxTransformUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.ArrayList

class PhotoSignActivity : BaseActivity<PhotoSignViewModel, ActivityPhotoSignBinding>() {
    private var loadingDialog: Dialog? = null
    private var imageFile: File? = null
    private var aid = ""
    private var token = ""
    private var objectId = ""
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        initArgs()
        initView()
        initObserver()
        doNetwork()
    }

    private fun initArgs() {
        aid = intent.getStringExtra("aid") ?: ""
    }

    private fun doNetwork() {
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.requestToken(URL.getUploadToken())
            analysisAndStartSign(aid)
        }
    }

    private fun initObserver() {
        // 获取上传token
        viewModel.tokenLiveData.observe(this) {
            val data = it.data?.body?.string()
            token = JSONObject.parseObject(data).getStringExt(Constants.Upload.TOKEN)
        }

        // 上传图片
        viewModel.uploadImageLiveData.observe(this) {
            lifecycleScope.launch(Dispatchers.IO) {
                val data = it.data?.body?.string()
                val itemData = JSONObject.parseObject(data)
                objectId = itemData.getStringExt(Constants.Upload.OBJECT_ID)
                sign()
            }
        }

        // 签到
        viewModel.signLiveData.observe(this) {
            lifecycleScope.launch(Dispatchers.IO) {
                val data = it.data?.body?.string()
                withContext(Dispatchers.Main) {
                    hideLoadingView()
                    data?.showSignResult()
                    imageFile?.delete()
                    finish()
                }
            }
        }
        viewModel.analysisLiveData.observe(this) {
            lifecycleScope.launch(Dispatchers.IO) {
                val data = it.data?.body?.string()
                val analysis2Code = data?.substringAfter("code='+'")?.substringBefore("'") ?: ""
                viewModel.analysis2(URL.getAnalysis2Path(analysis2Code))
            }

        }
    }

    private fun initView() {
        binding?.root?.setOnClickListener {
            PictureSelector
                .create(this)
                .openGallery(SelectMimeType.ofImage())
                .setSandboxFileEngine { context, srcPath, mineType, call ->
                    if (call != null) {
                        val sandboxPath =
                            SandboxTransformUtils.copyPathToSandbox(context, srcPath, mineType)
                        call.onCallback(srcPath, sandboxPath);
                    }
                }
                .setImageEngine(GlideEngine.createGlideEngine())
                .setMaxSelectNum(1)
                .forResult(object : OnResultCallbackListener<LocalMedia> {
                    override fun onResult(result: ArrayList<LocalMedia>?) {
                        result?.get(0)?.let {
                            lifecycleScope.launch(Dispatchers.IO) {
                                Downloader.download(
                                    this@PhotoSignActivity,
                                    it.path
                                ) { success, filepath ->
                                    if (success) {
                                        showLoadingView()
                                        imageFile = File(filepath)
                                        imageFile?.let {
                                            viewModel.uploadImage(URL.getUploadImagePath(token), it)
                                        }

                                    }
                                }
                            }
                        }
                    }

                    override fun onCancel() {}
                })
        }
    }

    private suspend fun analysisAndStartSign(aid: String) {
        viewModel.analysis(URL.getAnalysisPath(aid))
    }

    private fun sign() {
        val uid = CacheUtils.cache["uid"] ?: ""
        viewModel.sign(URL.getSignWithPhoto(aid, uid, objectId))
    }

    private fun showLoadingView() {
        lifecycleScope.launch(Dispatchers.Main) {
            loadingDialog = FullScreenDialog(this@PhotoSignActivity)
            loadingDialog?.setCancelable(false)
            loadingDialog?.show()
        }
    }

    private fun hideLoadingView() {
        loadingDialog?.dismiss()
        loadingDialog = null
    }
}
package com.cofbro.qian.scan

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Vibrator
import android.view.ViewGroup.MarginLayoutParams
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import cn.bingoogolapple.qrcode.core.QRCodeView
import com.cofbro.qian.databinding.ActivityScanBinding
import com.cofbro.qian.utils.Downloader
import com.cofbro.qian.utils.GlideEngine
import com.cofbro.qian.utils.dp2px
import com.cofbro.qian.utils.getStatusBarHeight
import com.king.wechat.qrcode.WeChatQRCodeDetector
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.luck.picture.lib.utils.SandboxTransformUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.opencv.OpenCV
import java.io.File


class ScanActivity : AppCompatActivity(), QRCodeView.Delegate {
    private var photoPath = ""
    private var binding: ActivityScanBinding? = null
    private val PERMISSION_SCAN = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA,
        Manifest.permission.VIBRATE
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        checkPermissions(this, PERMISSION_SCAN, 1)
        binding = ActivityScanBinding.inflate(layoutInflater, null, false)
        setContentView(binding?.root)
        OpenCV.initAsync(this)
        WeChatQRCodeDetector.init(this)
        initView()
        initEvent()
        binding?.scanner?.setDelegate(this)
    }

    private fun initEvent() {
        binding?.ivToPhoto?.setOnClickListener {
            toGalleryAndDecode()
        }

        binding?.ivBack?.setOnClickListener {
            finish()
        }
    }

    private fun initView() {
        val margin = getStatusBarHeight(this) + dp2px(this, 6)
        val params = binding?.ivToPhoto?.layoutParams as? MarginLayoutParams
        params?.topMargin = margin
        binding?.ivToPhoto?.layoutParams = params
    }

    override fun onScanQRCodeSuccess(result: String?) {
        vibrate()
        sendResult(result)
    }

    override fun onCameraAmbientBrightnessChanged(isDark: Boolean) {
        // 这里是通过修改提示文案来展示环境是否过暗的状态，接入方也可以根据 isDark 的值来实现其他交互效果
        var tipText: String? = binding?.scanner?.scanBoxView?.tipText
        if (tipText?.isEmpty() == true) return
        val ambientBrightnessTip = "\n环境过暗，请打开闪光灯"
        if (isDark) {
            if (!tipText!!.contains(ambientBrightnessTip)) {
                binding?.scanner?.scanBoxView?.tipText = tipText + ambientBrightnessTip
            }
        } else {
            if (tipText!!.contains(ambientBrightnessTip)) {
                tipText = tipText.substring(0, tipText.indexOf(ambientBrightnessTip))
                binding?.scanner?.scanBoxView?.tipText = tipText
            }
        }
    }

    override fun onScanQRCodeOpenCameraError() {

    }

    override fun onStart() {
        super.onStart()
        binding?.scanner?.startCamera() // 打开后置摄像头开始预览，但是并未开始识别
        binding?.scanner?.startSpotAndShowRect() // 显示扫描框，并开始识别
    }

    override fun onStop() {
        binding?.scanner?.stopCamera() // 关闭摄像头预览，并且隐藏扫描框
        super.onStop()
    }

    override fun onDestroy() {
        binding?.scanner?.destroyDrawingCache()
        binding?.scanner?.onDestroy()
        super.onDestroy()

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            binding?.scanner?.startCamera() // 打开后置摄像头开始预览，但是并未开始识别
            binding?.scanner?.startSpotAndShowRect() // 显示扫描框，并开始识别
        }
    }

    private fun checkPermissions(
        activity: Activity,
        permissions: Array<String>,
        requestCode: Int
    ): Boolean {
        var check: Int
        for (permission in permissions) {
            // 监测permission权限是否批准
            check = ContextCompat.checkSelfPermission(activity, permission)
            if (check != PackageManager.PERMISSION_GRANTED) {  //当前权限没开启
                // 请求系统弹窗，申请权限
                ActivityCompat.requestPermissions(activity, permissions, requestCode)
                return false
            }
        }
        return true
    }

    private fun toGalleryAndDecode() {
        PictureSelector
            .create(this)
            .openGallery(SelectMimeType.ofImage())
            .setSandboxFileEngine { context, srcPath, mineType, call ->
                if (call != null) {
                    val sandboxPath =
                        SandboxTransformUtils.copyPathToSandbox(context, srcPath, mineType)
                    call.onCallback(srcPath, sandboxPath)
                }
            }
            .setImageEngine(GlideEngine.createGlideEngine())
            .setMaxSelectNum(1)
            .forResult(object : OnResultCallbackListener<LocalMedia> {
                override fun onResult(result: ArrayList<LocalMedia>?) {
                    result?.get(0)?.let {
                        downloadPhoto(it.path)
                    }
                }

                override fun onCancel() {}
            })
    }

    private fun downloadPhoto(path: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            Downloader.download(this@ScanActivity, path) { success, filepath ->
                if (success) {
                    photoPath = filepath
                    val bitmap = BitmapFactory.decodeFile(photoPath)
                    //识别二维码
                    val results = WeChatQRCodeDetector.detectAndDecode(bitmap).getOrNull(0) ?: ""
                    lifecycleScope.launch(Dispatchers.Main) {
                        sendResult(results)
                    }
                }
            }
        }
    }

    private fun deletePhoto(filepath: String) {
        val file = File(filepath)
        if (file.exists()) {
            file.delete()
        }
    }

    private fun sendResult(result: String?) {
        val intent = Intent()
        intent.putExtra("result", result)
        setResult(RESULT_OK, intent)
        deletePhoto(photoPath)
        finish()
    }

    private fun vibrate() {
        val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(200)
    }
}
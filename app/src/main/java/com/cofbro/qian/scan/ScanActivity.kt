package com.cofbro.qian.scan

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Vibrator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import cn.bingoogolapple.qrcode.core.QRCodeView
import com.cofbro.qian.databinding.ActivityScanBinding


class ScanActivity : AppCompatActivity(), QRCodeView.Delegate {
    private var binding: ActivityScanBinding? = null
    private val PERMISSION_SCAN = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA,
        Manifest.permission.VIBRATE
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanBinding.inflate(layoutInflater, null, false)
        setContentView(binding?.root)
        checkPermissions(this, PERMISSION_SCAN, 1)
        binding?.scanner?.setDelegate(this)
    }

    override fun onScanQRCodeSuccess(result: String?) {
        vibrate()
        val intent = Intent()
        intent.putExtra("result", result)
        setResult(RESULT_OK, intent)
        finish()
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

    private fun vibrate() {
        val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(200)
    }
}
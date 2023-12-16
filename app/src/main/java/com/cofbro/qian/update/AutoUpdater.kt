package com.cofbro.qian.update

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.FileProvider
import com.cofbro.hymvvmutils.base.getBySp
import com.cofbro.hymvvmutils.base.saveUsedSp
import com.cofbro.qian.utils.Constants
import com.cofbro.qian.utils.parse2Long
import com.cofbro.qian.view.AutoUpdateTipDialog
import com.cofbro.qian.view.DownloadDialog
import com.hjq.toast.ToastUtils
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.ref.WeakReference
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.regex.Pattern


class AutoUpdater(private val mContext: Context) {
    private var apkFile = File(mContext.filesDir, "app-release-c.apk")
    private var apkUrl = "https://doraemon.halfsweet.cn/"
    private var checkUrl = "https://doraemon.halfsweet.cn/output-metadata.json"
    private var intercept = false
    private var downLoadThread: Thread? = null
    private var mProgress: ProgressBar? = null
    private var progress = 0f
    private var mTextStatus: TextView? = null
    private var mHandler: DownloadHandler? = null
    private val downApkWork = Runnable {
        try {
            val url = URL(apkUrl)
            HttpURLConnection.setFollowRedirects(true)
            val conn = url.openConnection() as HttpURLConnection
            conn.connect()
            val length = conn.contentLength
            val ins = conn.inputStream
            val fos = FileOutputStream(apkFile)
            var count = 0
            val buf = ByteArray(1024)
            while (true) {
                if (intercept) {
                    break
                }
                val numRead = ins.read(buf)
                count += numRead
                progress = count * 1f / length * 100.0f
                mHandler?.sendEmptyMessage(DOWN_UPDATE)
                if (numRead <= 0) {
                    mHandler?.sendEmptyMessage(DOWN_OVER)
                    break
                }
                fos.write(buf, 0, numRead)
            }
            fos.close()
            ins.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun checkUpdate(
        forced: Boolean = false,
        onPreCheck: () -> Unit = {},
        onShowDownloadDialog: () -> Unit = {}
    ) {
        onPreCheck.invoke()
        mHandler = DownloadHandler(this)
        Thread(Runnable {
            var localVersion = "1"
            try {
                localVersion =
                    mContext.packageManager.getPackageInfo(mContext.packageName, 0).versionName
                // 存储本地版本
                mContext.saveUsedSp(Constants.Update.LOCAL_VERSION, localVersion)
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }
            var remoteVersion = "1"
            var outputFile = ""
            val config = doGet(checkUrl)
            if (!config.isNullOrEmpty()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    var m = Pattern.compile("\"outputFile\":\\s*\"(?<m>[^\"]*?)\"").matcher(config)
                    if (m.find()) {
                        outputFile = m.group("m")?.toString() ?: ""
                    }
                    m = Pattern.compile("\"versionName\":\\s*\"(?<m>[^\"]*?)\"").matcher(config)
                    if (m.find()) {
                        remoteVersion = m.group("m")?.replace("v1.0.", "") ?: ""
                    }
                }
            }
            val tipShow = mContext.getBySp(Constants.Update.UPDATE_TIP)
            if (tipShow == Constants.Update.NO_SHOW && !forced) {
                return@Runnable
            }
            if (checkIfValidVersion(remoteVersion)) {
                apkUrl += outputFile
                mHandler?.sendEmptyMessage(DOWN_START)
                (mContext as? Activity)?.runOnUiThread(onShowDownloadDialog)
            } else {
                return@Runnable
            }
        }).start()
    }

    private fun checkIfValidVersion(remoteVersion: String): Boolean {
        // 存储远端最新版本号
        mContext.saveUsedSp(Constants.Update.REMOTE_VERSION, remoteVersion)
        val remoteUpdateVersion = parse2Long(remoteVersion)
        val localUpdateVersion = parse2Long(mContext.getBySp(Constants.Update.LOCAL_VERSION))
        if (localUpdateVersion == 0L || localUpdateVersion < remoteUpdateVersion) {
            return true
        }
        if (localUpdateVersion == remoteUpdateVersion) {
            apkFile.delete()
            return false
        }
        return false
    }

    private fun downloadApk() {
        val thread = Thread(downApkWork)
        downLoadThread = thread
        thread.start()
    }

    private fun doGet(httpUrl: String?): String? {
        var connection: HttpURLConnection? = null
        var `is`: InputStream? = null
        var br: BufferedReader? = null
        var result: String? = null
        try {
            val url = URL(httpUrl)
            connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 15000
            connection.readTimeout = 60000
            connection.connect()
            if (connection.responseCode == 200) {
                `is` = connection.inputStream
                br = BufferedReader(InputStreamReader(`is`, "UTF-8"))
                val sbf = StringBuffer()
                var temp: String? = null
                while (br.readLine().also { temp = it } != null) {
                    sbf.append(temp)
                    sbf.append("\r\n")
                }
                result = sbf.toString()
            }
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (null != br) {
                try {
                    br.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            if (null != `is`) {
                try {
                    `is`.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            connection!!.disconnect()
        }
        return result
    }

    @SuppressLint("SetTextI18n")
    private fun updateDownloadProgress() {
        val formattedNum = String.format("%.2f", progress)
        mTextStatus?.text = "$formattedNum%"
        mProgress?.progress = progress.toInt()
    }

    private fun installAPK() {
        try {
            if (!apkFile.exists()) {
                return
            }
            val intent = Intent("android.intent.action.VIEW")
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            if (Build.VERSION.SDK_INT >= 24) {
                val packageName = mContext.applicationContext.packageName
                val authority = "$packageName.fileprovider"
                val apkUri = FileProvider.getUriForFile(
                    mContext, authority,
                    apkFile
                )
                intent.setDataAndType(apkUri, "application/vnd.android.package-archive")
            } else {
                intent.setDataAndType(
                    Uri.fromFile(apkFile),
                    "application/vnd.android.package-archive"
                )
            }
            mContext.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun showUpdateDialog() {
        AutoUpdateTipDialog(mContext).apply {
            show()
            setOnPositiveListener {
                showDownloadDialog()
                dismiss()
            }
            setOnNegativeListener {
                dismiss()
            }
        }
    }

    private fun showDownloadDialog() {
        DownloadDialog(mContext).apply {
            setOnNegativeButtonListener {
                intercept = true
                dismiss()
            }
            setCancelable(false)
            show()
            mProgress = progress
            mTextStatus = textStatus
        }
        downloadApk()
    }

    companion object {
        private const val DOWN_UPDATE = 1
        private const val DOWN_OVER = 2
        private const val DOWN_START = 3
    }

    private class DownloadHandler(updater: AutoUpdater) : Handler(Looper.getMainLooper()) {
        private val mUpdateRef: WeakReference<AutoUpdater> = WeakReference(updater)

        override fun handleMessage(msg: Message) {
            val mUpdater = mUpdateRef.get() ?: return

            when (msg.what) {
                DOWN_UPDATE -> {
                    mUpdater.updateDownloadProgress()
                    return
                }

                DOWN_OVER -> {
                    ToastUtils.show("下载完毕")
                    mUpdater.installAPK()
                    return
                }

                DOWN_START -> {
                    mUpdater.showUpdateDialog()
                    return
                }

                else -> return
            }
        }
    }
}
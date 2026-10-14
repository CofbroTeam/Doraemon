package com.cofbro.qian.utils

import com.hjq.toast.ToastUtils
import java.io.File

object FileUtils {
    fun renameFile(file:File,newName:String){
        val fileExtension = file.path.substringAfterLast('.', "")
        val newFileName = file.parent?.plus("\\$newName.$fileExtension")
        if (file.exists()) {
            val newFile = newFileName?.let { File(it) }
            if (newFile?.let { file.renameTo(it) } == true) {
                ToastUtils.show("文件名更改成功")
            } else {
                ToastUtils.show("文件名更改失败")
            }
        } else {
            ToastUtils.show("文件不存在")
        }
    }
}
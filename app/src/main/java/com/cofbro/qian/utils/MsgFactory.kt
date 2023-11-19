package com.cofbro.qian.utils

import cn.leancloud.im.v2.LCIMMessage
import com.cofbro.qian.friend.im.IMClientUtils

object MsgFactory {
    fun createLCMessage(msg: String): LCIMMessage {
        val lcMsg = LCIMMessage("", IMClientUtils.getCntUser()?.objectId ?: "")
        lcMsg.content = "{\"_lctext\":\"${msg}\",\"_lctype\":-1}"
        lcMsg.timestamp = System.currentTimeMillis()
        return lcMsg
    }
}
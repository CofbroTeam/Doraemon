package com.cofbro.qian.utils

import cn.leancloud.LCObject
import cn.leancloud.im.v2.LCIMConversation
import cn.leancloud.im.v2.LCIMMessage
import com.alibaba.fastjson.JSONObject
import com.cofbro.qian.friend.im.IMClientUtils

object MsgFactory {
    fun createLCMessage(msg: String): LCIMMessage {
        val lcMsg = LCIMMessage("", IMClientUtils.getCntUser()?.objectId ?: "")
        lcMsg.content = "{\"_lctext\":\"${msg}\",\"_lctype\":-1}"
        lcMsg.timestamp = System.currentTimeMillis()
        return lcMsg
    }

    fun createConversationMsg(
        conv: LCIMConversation?,
        url: String = "",
        username: String = ""
    ): JSONObject {
        val data = JSONObject()
        data["conv"] = conv
        data["content"] = conv?.lastMessage?.content.toString()
        data["time"] = conv?.lastMessageAt?.time.toString()
        data["unReadCount"] = conv?.unreadMessagesCount.toString()
        if (url.isNotEmpty()) {
            data["avatar"] = url
        }
        if (username.isNotEmpty()) {
            data["username"] = username
        }
        return data
    }

    fun createUserInfoMsg(lcObject: LCObject): LCObject {
        val o = LCObject()
        o.put("name", lcObject.getString("username"))
        o.put("url", lcObject.getString("avatar"))
        o.put("uid", lcObject.objectId)
        return o
    }
}
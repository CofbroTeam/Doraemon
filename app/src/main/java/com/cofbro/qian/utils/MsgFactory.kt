package com.cofbro.qian.utils

import cn.leancloud.LCObject
import cn.leancloud.im.v2.LCIMConversation
import cn.leancloud.im.v2.LCIMMessage
import cn.leancloud.im.v2.messages.LCIMTextMessage
import com.alibaba.fastjson.JSONObject
import com.cofbro.qian.friend.im.IMClientUtils

object MsgFactory {
    const val who = "who"
    const val cookieSign = "cookie"
    const val agree = "agree"
    fun createNormalLCMessage(msg: String): LCIMMessage {
        val lcMsg = LCIMMessage("", IMClientUtils.getCntUser()?.objectId ?: "")
        lcMsg.content = "{\"_lctext\":\"${msg}\",\"_lctype\":-1}"
        lcMsg.timestamp = System.currentTimeMillis()
        return lcMsg
    }

    fun createCookieSignLCMessage(msg: String, cookie: String): LCIMTextMessage {
        val objectId = IMClientUtils.getCntUser()?.objectId ?: ""
        val lcMsg = LCIMTextMessage()
        val map = hashMapOf<String, Any>()
        map[cookieSign] = cookie
        map[who] = objectId
        lcMsg.from = objectId
        lcMsg.content = "{\"_lctext\":\"${msg}\",\"_lctype\":-1}"
        lcMsg.timestamp = System.currentTimeMillis()
        lcMsg.attrs = map
        return lcMsg
    }

    fun mockCookieSignLCMessage(oldMsg: LCIMTextMessage?, ifAgree: Boolean): LCIMTextMessage {
        val newMsg = LCIMTextMessage()
        val attrsMap = hashMapOf<String, Any>()
        attrsMap[cookieSign] = oldMsg?.attrs?.getOrDefault(cookieSign, "") ?: ""
        attrsMap[who] = oldMsg?.attrs?.getOrDefault(who, "") ?: ""
        attrsMap[agree] = if (ifAgree) "agree" else "refuse"
        newMsg.from = oldMsg?.from ?: ""
        newMsg.content = oldMsg?.content ?: ""
        newMsg.timestamp = oldMsg?.timestamp ?: 0L
        newMsg.attrs = attrsMap
        return newMsg
    }

    fun createConversationMsg(
        conv: LCIMConversation?,
        url: String = "",
        username: String = "",
        objectId: String = "",
    ): JSONObject {
        val data = JSONObject()
        data["conv"] = conv
        data["content"] = conv?.lastMessage?.content.toString()
        data["time"] = conv?.lastMessageAt?.time.toString()
        data["unReadCount"] = conv?.unreadMessagesCount.toString()
        if (objectId.isNotEmpty()) {
            data["objectId"] = objectId
        }
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

    fun createFriendRequestMsg(conversation: LCIMConversation?, friend: LCObject): JSONObject {
        val item = JSONObject()
        val isCreator = conversation?.creator == IMClientUtils.getCntUser()?.objectId
        item["username"] = friend.getString("username")
        item["avatar"] = friend.getString("avatar")
        item["uid"] = friend.getString("objectId")
        item["isCreator"] = isCreator
        item["content"] = if (isCreator) "好友申请已发送~" else "请求添加您为好友"
        item["status"] = conversation?.get("agree").toString()
        return item
    }
}
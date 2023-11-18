package com.cofbro.qian.friend.im

import cn.leancloud.im.v2.LCIMClient
import cn.leancloud.im.v2.LCIMConversation
import cn.leancloud.im.v2.LCIMMessage
import cn.leancloud.json.JSONObject

interface IEventCallback {
    fun onMessage(
        message: LCIMMessage?,
        conversation: LCIMConversation?,
        client: LCIMClient?
    )

    fun onInvite(
        client: LCIMClient?,
        conversation: LCIMConversation?,
        operator: String?
    )

    fun onInfoChanged(
        client: LCIMClient?,
        conversation: LCIMConversation?,
        attr: JSONObject?,
        operator: String?
    )
}
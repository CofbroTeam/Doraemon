package com.cofbro.qian.friend.im

import cn.leancloud.im.v2.LCIMConversation
import cn.leancloud.im.v2.LCIMMessage

interface IMessageDispatchEvent {
    fun onMessage(conv: LCIMConversation, message: LCIMMessage?,)

    fun getConversationId(): String
}
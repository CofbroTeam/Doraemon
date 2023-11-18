package com.cofbro.qian.friend.im

import cn.leancloud.im.v2.LCIMConversation

interface IMessageDispatchEvent {
    fun onMessage(conv: LCIMConversation)

    fun getConversationId(): String
}
package com.cofbro.qian.friend.chatActivity.observer

import cn.leancloud.im.v2.LCIMMessage

interface IConversationObserver {
    fun getConversationId(): String

    fun onMessage(message:LCIMMessage)
}
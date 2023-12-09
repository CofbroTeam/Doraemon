package com.cofbro.qian.friend.im

import cn.leancloud.im.v2.LCIMConversation
import cn.leancloud.im.v2.LCIMMessage

object MessageSubscriber {
    private val subscribeObject = arrayListOf<IMessageDispatchEvent>()

    fun subscribe(subscriber: IMessageDispatchEvent) {
        subscribeObject.add(subscriber)
    }

    fun dispatch(conversation: LCIMConversation?, message: LCIMMessage?,) {
        subscribeObject.forEach {
            if (conversation?.conversationId == it.getConversationId()) {
                it.onMessage(conversation, message)
            }
        }
    }

    fun unsubscribe(subscriber: IMessageDispatchEvent) {
        if (subscribeObject.contains(subscriber)) {
            subscribeObject.remove(subscriber)
        }
    }
}
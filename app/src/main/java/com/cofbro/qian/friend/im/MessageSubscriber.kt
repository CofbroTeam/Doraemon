package com.cofbro.qian.friend.im

import cn.leancloud.im.v2.LCIMConversation

object MessageSubscriber {
    private val subscribeObject = arrayListOf<IMessageDispatchEvent>()

    fun subscribe(subscriber: IMessageDispatchEvent) {
        subscribeObject.add(subscriber)
    }


    fun dispatch(conversation: LCIMConversation?) {
        subscribeObject.forEach {
            if (conversation?.conversationId == it.getConversationId()) {
                it.onMessage(conversation)
            }
        }
    }

    fun unSubscribe(subscriber: IMessageDispatchEvent) {
        if (subscribeObject.contains(subscriber)) {
            subscribeObject.remove(subscriber)
        }
    }
}
package com.cofbro.qian.friend.im

import cn.leancloud.im.v2.LCIMConversation
import cn.leancloud.im.v2.LCIMMessage

object MessageSubscriber {
    private val subscribeObject = arrayListOf<IMessageDispatchEvent>()
    private val conversations = hashMapOf<String, LCIMConversation?>()

    fun subscribe(subscriber: IMessageDispatchEvent) {
        subscribeObject.add(subscriber)
    }


    fun dispatch(conversation: LCIMConversation?, message: LCIMMessage?,) {
        val conversationId = conversation?.conversationId ?: ""
        if (!conversations.contains(conversationId)) {
            conversations[conversationId] = conversation
        }
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

    fun getConversationById(conversationId: String): LCIMConversation? {
        if (conversations.contains(conversationId)) {
            return conversations[conversationId]
        }
        return null
    }
}
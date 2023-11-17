package com.cofbro.qian.friend.chatActivity.observer

import cn.leancloud.im.v2.LCIMConversation

class ConversationObservable private constructor() {
    companion object {
        private var instance: ConversationObservable? = null
        fun getInstance(): ConversationObservable {
            if (instance == null) {
                synchronized(this) {
                    if (instance == null) {
                        instance = ConversationObservable()
                    }
                }
            }
            return instance!!
        }
    }

    private var conversationObservers: IConversationObserver? = null

    fun addConversationObserver(observer: IConversationObserver) {
        conversationObservers = observer
    }

    fun removeConversationObserver() {
        conversationObservers = null
    }

    fun notifyConversationObserver(conversation: LCIMConversation) {
        conversationObservers.let {
            if (it?.getConversationId() == conversation.conversationId) {
                it?.onMessage(conversation.lastMessage)
            }
        }
    }
}
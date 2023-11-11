package com.cofbro.qian.friend.im

import cn.leancloud.im.v2.LCIMClient
import cn.leancloud.im.v2.LCIMConversation
import cn.leancloud.im.v2.LCIMConversationEventHandler
import cn.leancloud.im.v2.LCIMMessage
import cn.leancloud.im.v2.LCIMMessageHandler
import cn.leancloud.im.v2.LCIMMessageManager
import cn.leancloud.im.v2.messages.LCIMTextMessage


object IMEventManager {
    private var initialized = false
    fun init(eventCallback: IEventCallback) {
        if (!initialized) {
            LCIMMessageManager.registerDefaultMessageHandler(CustomMessageHandler(eventCallback))
            LCIMMessageManager.setConversationEventHandler(CustomConversationEventHandler(eventCallback))
            initialized = true
        }
    }

    class CustomMessageHandler(private val eventCallback: IEventCallback) : LCIMMessageHandler() {
        override fun onMessage(
            message: LCIMMessage?,
            conversation: LCIMConversation?,
            client: LCIMClient?
        ) {
            if (message is LCIMTextMessage) {
                eventCallback.onMessage(message, conversation, client)
            }
        }
    }

    class CustomConversationEventHandler(private val eventCallback: IEventCallback) : LCIMConversationEventHandler() {
        override fun onMemberLeft(
            client: LCIMClient?,
            conversation: LCIMConversation?,
            members: MutableList<String>?,
            kickedBy: String?
        ) {
        }

        override fun onMemberJoined(
            client: LCIMClient?,
            conversation: LCIMConversation?,
            members: MutableList<String>?,
            invitedBy: String?
        ) {
        }

        override fun onKicked(
            client: LCIMClient?,
            conversation: LCIMConversation?,
            kickedBy: String?
        ) {
        }

        override fun onInvited(
            client: LCIMClient?,
            conversation: LCIMConversation?,
            operator: String?
        ) {
            eventCallback.onInvite(client, conversation, operator)
        }

    }
}
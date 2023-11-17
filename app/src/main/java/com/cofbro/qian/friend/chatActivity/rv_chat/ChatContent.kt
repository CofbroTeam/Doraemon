package com.cofbro.qian.friend.chatActivity.rv_chat

class ChatContent(val type: Int, val message: String? = null, val time: String) {
    companion object {
        const val TYPE_NORMAL_MYSELF = 0
        const val TYPE_NORMAL_FRIEND = 1
        const val TYPE_REQUEST_HOMEWORK_MYSELF = 10
        const val TYPE_REQUEST_HOMEWORK_FRIEND = 11
    }
}
package com.cofbro.qian.friend.im.chatActivity

import cn.leancloud.im.v2.LCIMConversation
import com.cofbro.hymvvmutils.base.BaseRepository
import com.cofbro.hymvvmutils.base.BaseViewModel

class ChatViewModel : BaseViewModel<BaseRepository>() {
    lateinit var conversation: LCIMConversation


}
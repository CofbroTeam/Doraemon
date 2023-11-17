package com.cofbro.qian.friend.chatActivity.rv_chat

// 为了减少 onBindViewHolder处的冗余代码而定义的接口
interface ViewHolderBind {
    fun bind(position: Int)
}
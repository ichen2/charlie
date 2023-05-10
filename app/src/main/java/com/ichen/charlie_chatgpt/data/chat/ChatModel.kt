package com.ichen.charlie_chatgpt.data.chat

class ChatModel(
    val message: String?,
    val isUser: Boolean,
    var numTokens: Int,
) {

    companion object {
        val Loading = ChatModel(null, false, 0)
    }
}

sealed class ChatStatus {
    object NotStarted: ChatStatus()
    object UserChatLoading: ChatStatus()
    object CharlieChatLoading: ChatStatus()
    object Loaded: ChatStatus()
    data class Error(val e: Exception): ChatStatus()
    fun isLoading(): Boolean = this is UserChatLoading || this is CharlieChatLoading
}
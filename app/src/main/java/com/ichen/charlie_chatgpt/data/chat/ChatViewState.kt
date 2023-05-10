package com.ichen.charlie_chatgpt.data.chat

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.model.ModelId
import com.ichen.charlie_chatgpt.BuildConfig

const val MAX_RESPONSE_NUM_TOKENS = 1000
const val MAX_NUM_TOKENS = 4000
const val CHARLIE_PROMPT =
    "You are Charlie, an AI assistant. " +
    "Please limit your responses to $MAX_RESPONSE_NUM_TOKENS tokens."
const val GPT_3_5_TURBO_ID = "gpt-3.5-turbo"
const val DEFAULT_API_KEY = BuildConfig.API_KEY

data class ChatViewState(
    var chatsInternal: List<ChatModel> = emptyList(),
    var chat: ChatStatus = ChatStatus.NotStarted,
    var key: Status<String> = Status.NotStarted,
    var customKey: String? = null,
    val prompt: String = CHARLIE_PROMPT,
    val modelId: String = GPT_3_5_TURBO_ID
) {

    val chats: List<ChatModel>
        get() = if (chat is ChatStatus.CharlieChatLoading) {
            chatsInternal + ChatModel.Loading
        } else {
            chatsInternal
        }

    private val totalNumTokens: Int
        get() = chatsInternal.fold(0) { cumulative, chat ->
            cumulative + chat.numTokens
        }

    val model: ModelId
        get() = ModelId(modelId)

    @OptIn(BetaOpenAI::class)
    val promptChatMessage: ChatMessage
        get() = ChatMessage(
            role = ChatRole.System,
            content = prompt,
        )

    fun addChat(newChat: ChatModel): List<ChatModel> {
        /*
            the maximum number of tokens that can be in our new chat after receiving a response
            calculated by adding:
            - the current number of tokens
            - the number of tokens in the new user chat
            - the max possible number of tokens in the new assistant chat
         */
        var maxPossibleNumTokens = totalNumTokens + newChat.numTokens + MAX_RESPONSE_NUM_TOKENS
        /*
            if our current messages + a max-length new messages would put us above the token limit,
            remove old messages until we'd be below the token limit
         */
        var index = 0
        while (maxPossibleNumTokens > MAX_NUM_TOKENS && index < chatsInternal.size) {
            maxPossibleNumTokens -= chatsInternal[index].numTokens
            index++
        }
        return chatsInternal.subList(index, chatsInternal.size) + newChat
    }
}

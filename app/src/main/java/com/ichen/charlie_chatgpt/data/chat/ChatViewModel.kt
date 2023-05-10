package com.ichen.charlie_chatgpt.data.chat

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.client.OpenAI
import com.knuddels.jtokkit.Encodings
import com.knuddels.jtokkit.api.Encoding
import com.knuddels.jtokkit.api.ModelType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ChatViewModel: ViewModel() {

    // view state
    var viewStateFlow = MutableStateFlow(ChatViewState())
    var encoding: Encoding = Encodings.newDefaultEncodingRegistry().getEncodingForModel(ModelType.GPT_3_5_TURBO)

    // OpenAI setup
    private val openAI: OpenAI
        get() = OpenAI(
            viewStateFlow.value.customKey ?: DEFAULT_API_KEY
        )

    fun loadKey(context: Context) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            try {
                val loadedKey: String? = context.dataStore.data.first()[DATA_STORE_KEY]
                loadedKey?.let {
                    viewStateFlow.update { oldState ->
                        oldState.copy(
                            key = Status.Loaded(it),
                            customKey = it
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("loadKey()", e.toString())
            }
        }
    }

    @OptIn(BetaOpenAI::class)
    fun validateKey(key: String, context: Context) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            viewStateFlow.update { oldState ->
                oldState.copy(
                    key = Status.Loading
                )
            }
            val request = ChatCompletionRequest(
                model = viewStateFlow.value.model,
                messages = listOf(ChatMessage(role = ChatRole.System, content = "")),
            )
            val testOpenAI = OpenAI(key)
            try {
                testOpenAI.chatCompletion(request)
                viewStateFlow.update { oldState ->
                    oldState.copy(
                        key = Status.Loaded(key),
                        customKey = key,
                    )
                }
                context.dataStore.edit { dataStore ->
                    dataStore[DATA_STORE_KEY] = key
                }
            } catch(e: Exception) {
                Log.e("validateKey()", e.toString())
                viewStateFlow.update { oldState ->
                    oldState.copy(
                        key = Status.Error(e),
                    )
                }
            }
        }
    }

    @OptIn(BetaOpenAI::class)
    fun onSendMessage(userMessage: String) = viewModelScope.launch {
        viewStateFlow.update { oldState ->
            oldState.copy(
                chat = ChatStatus.UserChatLoading,
            )
        }
        withContext(Dispatchers.IO) {
            // get user chat
            val userChat = ChatModel(
                message = userMessage,
                isUser = true,
                numTokens = getNumTokens(userMessage),
            )
            // display user chat and loading UI
            viewStateFlow.update { oldState ->
                oldState.copy(
                    chatsInternal = oldState.addChat(userChat),
                    chat = ChatStatus.CharlieChatLoading,
                )
            }
            // create request
            val request = ChatCompletionRequest(
                model = viewStateFlow.value.model,
                messages = listOf(viewStateFlow.value.promptChatMessage) + viewStateFlow.value.chatsInternal.toChatMessages()
            )
            try {
                // execute request
                val completion = openAI.chatCompletion(request)
                // parse response
                val assistantMessage = completion.choices[0].message?.content
                val assistantChat = ChatModel(
                    message = assistantMessage,
                    isUser = false,
                    numTokens = completion.usage?.completionTokens ?: getNumTokens(assistantMessage)
                )
                // display assistant chat and hide loading UI
                viewStateFlow.update { oldState ->
                    oldState.copy(
                        chatsInternal = oldState.addChat(assistantChat),
                        chat = ChatStatus.Loaded
                    )
                }
            } catch(e: Exception) {
                Log.e("onSendMessage()", e.toString())
                viewStateFlow.update { oldState ->
                    oldState.copy(
                        chat = ChatStatus.Error(e)
                    )
                }
            }
        }
    }

    // helper function to make a ChatModel and calculate it's numTokens
    private fun getNumTokens(message: String?) : Int = message?.let { encoding.encode(it).size } ?: 0

    companion object {
        private val Context.dataStore : DataStore<Preferences> by preferencesDataStore(
            name = "user"
        )
        private val DATA_STORE_KEY = stringPreferencesKey("api_key")
    }
}

@OptIn(BetaOpenAI::class)
fun List<ChatModel>.toChatMessages() = this.mapNotNull { chatModel ->
    chatModel.message?.let { message ->
        ChatMessage(
            role = if (chatModel.isUser) ChatRole.User else ChatRole.Assistant,
            content = message,
        )
    }
}
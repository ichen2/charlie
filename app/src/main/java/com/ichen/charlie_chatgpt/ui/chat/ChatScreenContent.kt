package com.ichen.charlie_chatgpt.ui.chat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ichen.charlie_chatgpt.data.chat.ChatModel
import com.ichen.charlie_chatgpt.data.chat.ChatStatus
import com.ichen.charlie_chatgpt.ui.chat.components.ChatView
import com.ichen.charlie_chatgpt.ui.chat.components.ErrorView

@Composable
fun ColumnScope.ChatScreenContent(
    listState: LazyListState,
    chat: ChatStatus,
    chats: List<ChatModel>,
    hasCustomKey: Boolean,
) {
    LazyColumn(
        modifier = Modifier
            .weight(1f)
            .padding(
                horizontal = 32.dp
            ),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        state = listState,
    ) {
        item {
            Spacer(Modifier.height(4.dp))
        }
        items(items = chats) { chat ->
            ChatView(message = chat.message, isUser = chat.isUser)
        }
        (chat as? ChatStatus.Error)?.e?.let { exception ->
            item {
                ErrorView(exception, hasCustomKey)
            }
        }
        item {
            Spacer(Modifier.height(4.dp))
        }
    }
}
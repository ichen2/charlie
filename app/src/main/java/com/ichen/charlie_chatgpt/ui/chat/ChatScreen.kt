package com.ichen.charlie_chatgpt.ui.chat

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ichen.charlie_chatgpt.data.chat.ChatViewModel
import com.ichen.charlie_chatgpt.data.chat.Status
import com.ichen.charlie_chatgpt.ui.theme.CharlieTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ChatScreen(
    viewModel: ChatViewModel = viewModel()
) {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    val focusManager = LocalFocusManager.current

    val coroutineScope = rememberCoroutineScope()

    val viewState by viewModel.viewStateFlow.collectAsState()
    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        confirmValueChange = { it != ModalBottomSheetValue.HalfExpanded },
        skipHalfExpanded = true
    )
    val listState = rememberLazyListState()

    var chatTextFieldValue by remember { mutableStateOf("") }
    var keyTextFieldValue by remember { mutableStateOf("") }

    // load cached key
    LaunchedEffect(Unit) {
        viewModel.loadKey(context)
    }

    // scroll to new chats
    LaunchedEffect(viewState.chats) {
        if (viewState.chats.isNotEmpty()) {
            listState.animateScrollToItem(viewState.chats.size - 1)
        }
    }

    // hide the bottom sheet and keyboard if key is validated
    LaunchedEffect(viewState.key) {
        (viewState.key as? Status.Loaded)?.data?.let {
            sheetState.hide()
            focusManager.clearFocus()
        }
    }

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetContent = {
            SetKeySheet(
                textFieldValue = keyTextFieldValue,
                onTextFieldValueChange = { newValue -> keyTextFieldValue = newValue },
                key = viewState.key,
                hasCustomKey = viewState.customKey != null,
                validateKey = { viewModel.validateKey(keyTextFieldValue, context) },
                openWebsite = { uriHandler.openUri("https://platform.openai.com/account/api-keys") }
            )
        },
        sheetShape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        sheetBackgroundColor = CharlieTheme.colors.surface,
        sheetContentColor = CharlieTheme.colors.onSurface,
        scrimColor = Color.Black.copy(alpha = 0.8f)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(CharlieTheme.colors.background),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            ChatScreenHeader(
                onKeyClick = { coroutineScope.launch { sheetState.show() } },
            )
            ChatScreenContent(
                listState = listState,
                chat = viewState.chat,
                chats = viewState.chats,
                hasCustomKey = viewState.customKey != null,
            )
            ChatScreenFooter(
                textFieldValue = chatTextFieldValue,
                onTextFieldValueChange = { newValue -> chatTextFieldValue = newValue },
                chat = viewState.chat,
                onSend = {
                    val message = chatTextFieldValue
                    chatTextFieldValue = ""
                    viewModel.onSendMessage(message)
                }
            )
        }
    }
}

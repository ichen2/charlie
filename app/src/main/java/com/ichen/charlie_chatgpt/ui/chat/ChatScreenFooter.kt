package com.ichen.charlie_chatgpt.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.ichen.charlie_chatgpt.data.chat.ChatStatus
import com.ichen.charlie_chatgpt.ui.theme.CharlieTheme

@Composable
fun ChatScreenFooter(
    textFieldValue: String,
    onTextFieldValueChange: (String) -> Unit,
    chat: ChatStatus,
    onSend: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(CharlieTheme.colors.surface)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BasicTextField(
            modifier = Modifier.fillMaxWidth(),
            value = textFieldValue,
            onValueChange = onTextFieldValueChange,
            cursorBrush = SolidColor(CharlieTheme.colors.onBackground),
            textStyle = TextStyle(color = CharlieTheme.colors.onBackground),
            decorationBox = { textfield ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(CharlieTheme.colors.background)
                        .padding(start = 24.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(Modifier.weight(1f)) {
                        textfield()
                    }
                    Spacer(Modifier.width(4.dp))
                    Box(
                        modifier = Modifier
                            .align(Alignment.Top)
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(CharlieTheme.colors.primary)
                            .clickable(enabled = !chat.isLoading(), onClick = onSend),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowUpward,
                            modifier = Modifier
                                .size(20.dp),
                            tint = CharlieTheme.colors.onPrimary,
                            contentDescription = "send",
                        )
                    }
                }
            }
        )
    }
}
package com.ichen.charlie_chatgpt.ui.chat

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.ichen.charlie_chatgpt.data.chat.Status
import com.ichen.charlie_chatgpt.ui.theme.CharlieTheme

@Composable
fun SetKeySheet(
    textFieldValue: String,
    onTextFieldValueChange: (String) -> Unit,
    key: Status<String>,
    hasCustomKey: Boolean,
    validateKey: () -> Unit,
    openWebsite: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Use your own API key",
            color = CharlieTheme.colors.onSurface,
            style = CharlieTheme.typography.h6,
        )
        if (!hasCustomKey) {
            Spacer(Modifier.height(16.dp))
            ClickableText(
                style = TextStyle.Default.copy(textAlign = TextAlign.Center),
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = CharlieTheme.colors.onSurface)) {
                        append("You are currently using Charlie's free tier, which has usage limits. ")
                        append("To get unlimited usage, you can get your own key from the ")
                    }
                    withStyle(
                        SpanStyle(color = CharlieTheme.colors.onSurface, fontWeight = FontWeight.Bold, textDecoration = TextDecoration.Underline)
                    ) {
                        append("OpenAI website")
                    }
                    withStyle(SpanStyle(color = CharlieTheme.colors.onSurface)) {
                        append(", and paste it here.")
                    }
                },
                onClick = { openWebsite() },
            )
        }
        Spacer(Modifier.height(32.dp))
        BasicTextField(
            modifier = Modifier.fillMaxWidth(),
            value = textFieldValue,
            onValueChange = onTextFieldValueChange,
            cursorBrush = SolidColor(CharlieTheme.colors.onBackground),
            textStyle = CharlieTheme.typography.body1.copy(
                color = CharlieTheme.colors.onBackground,
                textAlign = TextAlign.Center,
            ),
            maxLines = 1,
            singleLine = true,
            decorationBox = { textfield ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(CharlieTheme.colors.background)
                        .padding(horizontal = 24.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    if (textFieldValue.isEmpty()) {
                        Text(
                            text = "Paste API key here",
                            style = CharlieTheme.typography.body1.copy(
                                color = CharlieTheme.colors.onBackground.copy(alpha = 0.5f),
                                textAlign = TextAlign.Center,
                            ),
                        )
                    }
                    textfield()
                }
            },
            keyboardActions = KeyboardActions(
                onDone = { validateKey() },
            ),
        )
        (key as? Status.Error)?.e?.message?.let { errorMessage ->
            Spacer(Modifier.height(16.dp))
            Text(
                text = errorMessage ?: "Invalid API Key",
                color = CharlieTheme.colors.primary,
            )
        }
        Spacer(Modifier.height(32.dp))

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(24.dp)),
            onClick = validateKey,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = CharlieTheme.colors.primary,
                contentColor = CharlieTheme.colors.primary,
            )
        ) {
            if (key is Status.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = CharlieTheme.colors.onPrimary,
                )
            } else {
                Text(
                    text = "Save",
                    color = CharlieTheme.colors.onPrimary,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}
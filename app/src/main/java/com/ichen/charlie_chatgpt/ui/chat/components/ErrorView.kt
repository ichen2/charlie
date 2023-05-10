package com.ichen.charlie_chatgpt.ui.chat.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.ContextCompat.startActivity
import com.aallam.openai.api.exception.OpenAIAPIException
import com.aallam.openai.api.exception.OpenAIError
import com.aallam.openai.api.exception.OpenAIErrorDetails
import com.ichen.charlie_chatgpt.BuildConfig
import com.ichen.charlie_chatgpt.data.now
import com.ichen.charlie_chatgpt.ui.theme.CharlieTheme

const val EMAIL_ADDRESS = "charlieaichatbot@gmail.com"
const val EMAIL_SUBJECT = "Issue Report"
const val EMAIL_TEMPLATE = """
Version: %d
Date: %s
Error code: %d
Error message %s

Description of issue:
"""

const val API_KEY_ERROR_MESSAGE_UPSELL =
    "Looks like you're usage is exceeding the free tier limits. " +
    "If you'd like to continue using Charlie without any limits, " +
    "you can use your own API key by clicking the icon in the top right."

@Composable
fun ErrorView(
    exception: Exception,
    hasCustomKey: Boolean,
) = Row(
    modifier = Modifier
        .clip(
            RoundedCornerShape(24.dp)
        )
        .height(IntrinsicSize.Max)
        .background(CharlieTheme.colors.surface),
    verticalAlignment = Alignment.CenterVertically,
) {
    val context = LocalContext.current
    val errorMessage: AnnotatedString = buildAnnotatedString {
        withStyle(style = SpanStyle(color = CharlieTheme.colors.onSurface)) {
            append(exception.message ?: "Unknown error")
            append("\n\n")
        }
        // If the error is experiencing issues with the default API key, ask them to upload their own key
        if (
            exception is OpenAIAPIException &&
            !hasCustomKey &&
            (exception.statusCode == 429 || exception.statusCode == 401)
        ) {
            withStyle(
                style = SpanStyle(
                    color = CharlieTheme.colors.onSurface,
                )
            ) {
                append(API_KEY_ERROR_MESSAGE_UPSELL)
                append("\n\n")
            }
        }
        withStyle(style = SpanStyle(color = CharlieTheme.colors.onSurface, textDecoration = TextDecoration.Underline, fontWeight = FontWeight.Bold)) {
            append("Contact support")
        }

    }
    Box(
        modifier = Modifier
            .width(48.dp)
            .fillMaxHeight()
            .background(CharlieTheme.colors.primary),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Filled.Error,
            modifier = Modifier
                .size(32.dp),
            tint = CharlieTheme.colors.onPrimary,
            contentDescription = "",
        )
    }
    Column(
        modifier = Modifier
            .weight(1f)
            .clickable { handleEmailClicked(exception, context) }
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Text(
            text = errorMessage,
        )
    }
}

fun handleEmailClicked(exception: Exception, context: Context) {
    try {
        // build email intent
        val formattedEmailTemplate = EMAIL_TEMPLATE.format(
            BuildConfig.VERSION_CODE,
            now(),
            (exception as? OpenAIAPIException)?.statusCode ?: -1,
            exception.message ?: "Unknown error",
        )
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(EMAIL_ADDRESS))
            putExtra(Intent.EXTRA_SUBJECT, EMAIL_SUBJECT)
            putExtra(Intent.EXTRA_TEXT, formattedEmailTemplate)
        }
        // check if user has an app to handle the email intent
        if (intent.resolveActivity(context.packageManager) != null) {
            // open email app
            context.startActivity(intent)
        } else {
            // copy email address to clipboard
            val clipboard = getSystemService(context, ClipboardManager::class.java) as ClipboardManager
            val clip = ClipData.newPlainText("support_email", EMAIL_ADDRESS)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(context, "Support email copied to clipboard!", Toast.LENGTH_SHORT).show()
        }
    } catch (e: Exception) {
        Log.e("openSupportEmail()", e.toString())
    }
}

@Preview
@Composable
fun ErrorPreview() {
    ErrorView(
        OpenAIAPIException(
                statusCode = 429,
                error = OpenAIError(detail = OpenAIErrorDetails(code = "", "Error", "", "")),
                throwable = null,
        ),
        false
    )
}

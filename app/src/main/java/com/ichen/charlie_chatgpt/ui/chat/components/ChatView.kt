package com.ichen.charlie_chatgpt.ui.chat.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ichen.charlie_chatgpt.ui.theme.CharlieTheme

@Composable
fun ChatView(
    message: String?,
    isUser: Boolean,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if(isUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .clip(
                    RoundedCornerShape(
                        topStart = 24.dp,
                        topEnd = 24.dp,
                        bottomStart = if (isUser) 24.dp else 0.dp,
                        bottomEnd = if (isUser) 0.dp else 24.dp,
                    )
                )
                .background(if (isUser) CharlieTheme.colors.primary else CharlieTheme.colors.surface)
                .padding(horizontal = 24.dp, vertical = 16.dp),
        ) {
            if (message != null) {
                Text(
                    text = message,
                    color = if (isUser) CharlieTheme.colors.onPrimary else CharlieTheme.colors.onSurface,
                )
            } else {
                Loading()
            }
        }
    }
}

const val MAX_NUM_DOTS = 3
const val MIN_NUM_DOTS = 0

@Composable
fun Loading() {
    val infiniteTransition = rememberInfiniteTransition()
    val numDots by infiniteTransition.animateInt(
        initialValue = MIN_NUM_DOTS,
        targetValue = MAX_NUM_DOTS + 1,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        for (i in MIN_NUM_DOTS .. MAX_NUM_DOTS) {
            Text(
                text = ".",
                color = if (numDots > i) CharlieTheme.colors.onSurface else Color.Transparent,
            )
        }
    }
}

@Composable
fun InfiniteTransition.animateInt(
    initialValue: Int,
    targetValue: Int,
    animationSpec: InfiniteRepeatableSpec<Int>
): State<Int> = animateValue(initialValue, targetValue, Int.VectorConverter, animationSpec)